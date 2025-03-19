package ru.spbstu.rakitin.management.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.spbstu.rakitin.commonstarter.dto.TaskType;
import ru.spbstu.rakitin.management.service.JobService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Configuration
public class JobsConfiguration {

    @Bean
    public ThreadPoolExecutor jobThreadPoolExecutor() {
        return (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }

    @Bean
    public Map<TaskType, JobService<?>> jobServiceMap(List<JobService<?>> jobServices) {
        return jobServices.stream().collect(Collectors.toMap(JobService::getTaskType, jobService -> jobService));
    }
}
