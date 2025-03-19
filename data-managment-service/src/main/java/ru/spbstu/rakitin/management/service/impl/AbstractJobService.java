package ru.spbstu.rakitin.management.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.processor.api.Processor;
import org.springframework.beans.factory.BeanFactory;
import ru.spbstu.rakitin.commonentites.model.Project;
import ru.spbstu.rakitin.commonentites.model.Topic;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.commonstarter.dto.JobDto;
import ru.spbstu.rakitin.commonstarter.dto.JobNameDto;
import ru.spbstu.rakitin.commonstarter.utils.MapJson;
import ru.spbstu.rakitin.management.dto.KafkaJobStream;
import ru.spbstu.rakitin.management.engine.processors.AddTimestampFieldAction;
import ru.spbstu.rakitin.management.engine.processors.ExpressionFilter;
import ru.spbstu.rakitin.management.engine.processors.RemoveExtraFieldsAction;
import ru.spbstu.rakitin.management.engine.processors.SchemaCompatibleFilter;
import ru.spbstu.rakitin.management.exception.TaskAlreadyInContextException;
import ru.spbstu.rakitin.management.service.JobService;
import ru.spbstu.rakitin.management.service.KafkaService;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractJobService<T extends JobDto<?>> implements JobService<T> {


    private Map<String, KafkaJobStream> runningKafkaStreams;
    private final AdminManager adminManager;
    private final BeanFactory beanFactory;
    private final KafkaService kafkaService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final MapJson INVALID_JSON_FILTER = new MapJson();


    @Override
    public void startJob(T job) throws Exception {
        Project project = adminManager.findProjectById(job.getProjectId());
        String taskName = project.getProjectName() + "." + job.getTaskName();
        if (runningKafkaStreams.containsKey(taskName)) {
            throw new TaskAlreadyInContextException(String.format("Task with name %s already contains in context", taskName));
        }
        Properties kafkaProperties = beanFactory.getBean("kafkaProperties", Properties.class);
        kafkaProperties.put("group.id", taskName);
        kafkaProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, taskName);
        Topic topic = kafkaService.findTopicById(job.getTopicId());


        StreamsBuilder streamsBuilder = buildStream(job, taskName, topic);

        KafkaStreams streams = new KafkaStreams(streamsBuilder.build(), kafkaProperties);
        streams.setUncaughtExceptionHandler(exception -> {
            log.error("Error occurred for task {}. Changing status from RUNNING to FAILED...", job.getInstanceId(), exception);
            try {
                changeTaskStatus(job.getInstanceId(), "FAILED");
            } catch (Exception e) {
                log.error("Unable to change status for task {}. Please, do it manually in task service!", job.getInstanceId(), e);
            }
            this.runningKafkaStreams.remove(taskName);
            return StreamsUncaughtExceptionHandler.StreamThreadExceptionResponse.SHUTDOWN_CLIENT;
        });
        streams.start();
        runningKafkaStreams.put(taskName, KafkaJobStream.builder()
                .job(job)
                .kafkaStreams(streams).build());

    }

    private StreamsBuilder buildStream(T job, String taskName, Topic topic) {
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        ExpressionFilter expressionFilter = new ExpressionFilter(job.getSchema());
        streamsBuilder.stream(topic.getNameInKafka(), Consumed.with(Serdes.String(), Serdes.String()))
                .mapValues((readOnlyKey, value) -> {
                    try {
                        return objectMapper.readValue(value, MapJson.class);
                    } catch (JsonProcessingException e) {
                        log.warn("[{}] Skip value [{}]. Unable to parse it to json object.", taskName, value, e);
                        return INVALID_JSON_FILTER;
                    }
                }).filter((key, value) -> value != INVALID_JSON_FILTER)
                .peek(new RemoveExtraFieldsAction(job.getSchema()))
                .filter(new SchemaCompatibleFilter(job.getSchema(), taskName))
                .peek(new AddTimestampFieldAction(job.getSchema()))
                .split().branch(expressionFilter, Branched.withConsumer(stream -> {
                    decorateStream(job, taskName, stream).process(() -> getTaskProcessor(job, taskName));
                })).defaultBranch(Branched.withConsumer(stream -> {
                    stream.foreach((key, value) -> log.info("[{}] Message [{}] was skipped because not apply task filter!", taskName, value));
                }));

        return streamsBuilder;
    }

    @Override
    public void stopJob(JobNameDto jobName) {
        String taskName = jobName.getProjectName() + "." + jobName.getTaskName();
        KafkaStreams streams = Objects.requireNonNull(this.runningKafkaStreams.get(taskName), String.format("Job with name %s not found in tasks list", taskName)).getKafkaStreams();
        streams.close();
        this.runningKafkaStreams.remove(taskName);
    }

    @PostConstruct
    @Override
    public void init() {
        this.runningKafkaStreams = new HashMap<>();
        Thread thread = new Thread(new FetchFromService(this));
        thread.setDaemon(true);
        thread.setName("task-fetcher-" + getServiceName());
        thread.start();

    }

    @Override
    public List<JobDto<?>> getJobs() {
        return runningKafkaStreams.values().stream().map(KafkaJobStream::getJob).collect(Collectors.toList());
    }

    public long getFetchTasksRetryTimeoutMillis() {
        return 30 * 1000;
    }

    @PreDestroy
    @Override
    public void onShutdown() {
        runningKafkaStreams.values().stream().map(KafkaJobStream::getKafkaStreams).forEach(KafkaStreams::close);
    }

    protected abstract List<T> fetchRunningTasks();

    protected abstract void changeTaskStatus(long taskId, String status);

    protected abstract Processor<String, MapJson, String, String> getTaskProcessor(T job, String taskName);

    protected KStream<String, MapJson> decorateStream(T job, String taskName, KStream<String, MapJson> stream) {
        return stream;
    }

    protected abstract String getServiceName();

    @RequiredArgsConstructor
    private final class FetchFromService implements Runnable {

        private final AbstractJobService<T> jobService;

        @Override
        public void run() {
            boolean fetched = false;
            List<T> runningJobs = null;
            while (!fetched) {
                try {
                    log.info("Try to fetch all running tasks from {} service...", jobService.getServiceName());
                    runningJobs = jobService.fetchRunningTasks();
                    log.info("Tasks with id {} was fetched!", runningJobs.stream().map(JobDto::getInstanceId).toList());
                    fetched = true;
                } catch (Throwable e) {
                    log.error("Unable to fetch all running tasks from {} service. Waiting for {} millis", jobService.getServiceName(), jobService.getFetchTasksRetryTimeoutMillis(), e);
                    try {
                        Thread.sleep(jobService.getFetchTasksRetryTimeoutMillis());
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            for (T jobDto : runningJobs) {
                try {
                    jobService.startJob(jobDto);
                } catch (TaskAlreadyInContextException taskAlreadyInContextException) {
                    log.warn("Unable to start task with id {} cause it already started", jobDto.getInstanceId());
                } catch (Exception e) {
                    log.error("Error occurred for {} task {}. Changing status from RUNNING to FAILED...", jobService.getServiceName(), jobDto.getInstanceId(), e);
                    jobService.changeTaskStatus(jobDto.getInstanceId(), "FAILED");
                }
            }
        }
    }

}
