package ru.spbstu.rakitin.management.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class JobsConfiguration {

    @Bean
    public ThreadPoolExecutor jobThreadPoolExecutor() {
        return (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }
}
