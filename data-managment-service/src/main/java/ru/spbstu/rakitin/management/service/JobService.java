package ru.spbstu.rakitin.management.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import ru.spbstu.rakitin.commonstarter.dto.JobDto;
import ru.spbstu.rakitin.commonstarter.dto.JobNameDto;

public interface JobService<T extends JobDto> {

    void startJob(T job) throws Exception;
    void stopJob(JobNameDto jobName);

    @PostConstruct
    void init();

    @PreDestroy
    void onShutdown();

}
