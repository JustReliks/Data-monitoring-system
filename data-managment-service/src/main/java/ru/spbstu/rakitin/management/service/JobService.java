package ru.spbstu.rakitin.management.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import ru.spbstu.rakitin.dto.JobDto;
import ru.spbstu.rakitin.dto.JobNameDto;
import ru.spbstu.rakitin.dto.TaskType;
import ru.spbstu.rakitin.management.exception.JobDisabledException;

import java.util.List;

public interface JobService<T extends JobDto<?>> {

    void startJob(T job) throws Exception;
    void stopJob(JobNameDto jobName) throws JobDisabledException;
    TaskType getTaskType();
    List<JobDto<?>> getJobs();

    @PostConstruct
    void init();

    @PreDestroy
    void onShutdown();

}
