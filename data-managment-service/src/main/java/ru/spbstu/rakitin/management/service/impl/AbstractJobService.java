package ru.spbstu.rakitin.management.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.processor.api.Processor;
import org.springframework.beans.factory.BeanFactory;
import ru.spbstu.rakitin.commonentites.model.Project;
import ru.spbstu.rakitin.commonentites.model.Topic;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.commonstarter.dto.JobDto;
import ru.spbstu.rakitin.commonstarter.dto.JobNameDto;
import ru.spbstu.rakitin.management.exception.TaskAlreadyInContextException;
import ru.spbstu.rakitin.management.service.JobService;
import ru.spbstu.rakitin.management.service.KafkaService;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractJobService<T extends JobDto> implements JobService<T> {

    public static final int RETRY_TIMEOUT = 4000;

    private Map<String, KafkaStreams> runningKafkaStreams;
    private final AdminManager adminManager;
    private final BeanFactory beanFactory;
    private final KafkaService kafkaService;


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
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        Topic topic = kafkaService.findTopicById(job.getTopicId());


        KStream<String, String> sourceStream = streamsBuilder.stream(topic.getNameInKafka(), Consumed.with(Serdes.String(), Serdes.String()));
        sourceStream.process(() -> this.getTaskProcessor(job, taskName));

        KafkaStreams streams = new KafkaStreams(streamsBuilder.build(), kafkaProperties);
        streams.setUncaughtExceptionHandler(exception -> {
            log.error("Error occurred for task {}. Changing status from RUNNING to FAILED...", job.getInstanceId(), exception);
            try {
                changeTaskStatus(job.getInstanceId(), "FAILED");
            } catch (Exception e) {
                log.error("Unable to change status for task {}. Please, do it manually in task service!", job.getInstanceId(), e);
                return StreamsUncaughtExceptionHandler.StreamThreadExceptionResponse.REPLACE_THREAD;
            }
            this.runningKafkaStreams.remove(taskName);
            return StreamsUncaughtExceptionHandler.StreamThreadExceptionResponse.SHUTDOWN_CLIENT;
        });
        streams.start();
        runningKafkaStreams.put(taskName, streams);

    }

    @Override
    public void stopJob(JobNameDto jobName) {
        String taskName = jobName.getProjectName() + "." + jobName.getTaskName();
        KafkaStreams streams = Objects.requireNonNull(this.runningKafkaStreams.get(taskName), String.format("Job with name %s not found in tasks list", taskName));
        streams.close();
        this.runningKafkaStreams.remove(taskName);
    }

    @PostConstruct
    @Override
    public void init() {
        this.runningKafkaStreams = new HashMap<>();
        Thread thread = new Thread(new FetchFromService(this));
        thread.setDaemon(true);
        thread.setName("task-fetcher");
        thread.start();

    }

    @PreDestroy
    @Override
    public void onShutdown() {
        runningKafkaStreams.values().forEach(KafkaStreams::close);
    }

    protected abstract List<T> fetchRunningTasks();

    protected abstract void changeTaskStatus(long taskId, String status);

    protected abstract Processor<String, String, String, String> getTaskProcessor(T job, String taskName);

    @RequiredArgsConstructor
    private final class FetchFromService implements Runnable {

        private final AbstractJobService<T> jobService;

        @Override
        public void run() {
            boolean fetched = false;
            List<T> runningJobs = null;
            while (!fetched) {
                try {
                    log.info("Try to fetch all running tasks from fulltext service...");
                    runningJobs = jobService.fetchRunningTasks();
                    log.info("Tasks with id {} was fetched!", runningJobs.stream().map(JobDto::getInstanceId).toList());
                    fetched = true;
                } catch (Throwable e) {
                    log.error("Unable to fetch all running tasks from fulltext service. Waiting for {} millis", RETRY_TIMEOUT, e);
                    try {
                        Thread.sleep(RETRY_TIMEOUT);
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
                    log.error("Error occurred for fulltext task {}. Changing status from RUNNING to FAILED...", jobDto.getInstanceId(), e);
                    jobService.changeTaskStatus(jobDto.getInstanceId(), "FAILED");
                }
            }
        }
    }

}
