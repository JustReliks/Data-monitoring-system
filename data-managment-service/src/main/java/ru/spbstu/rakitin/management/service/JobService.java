package ru.spbstu.rakitin.management.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import ru.spbstu.rakitin.commonstarter.dto.JobDto;
import ru.spbstu.rakitin.commonstarter.dto.JobNameDto;
import ru.spbstu.rakitin.commonstarter.dto.TaskType;

import java.util.List;

public interface JobService<T extends JobDto<?>> {

    void startJob(T job) throws Exception;
    void stopJob(JobNameDto jobName);
    TaskType getTaskType();
    List<JobDto<?>> getJobs();

    @PostConstruct
    void init();

    @PreDestroy
    void onShutdown();

}
