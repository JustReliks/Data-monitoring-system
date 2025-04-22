package ru.spbstu.rakitin.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.spbstu.rakitin.dto.TaskClientDto;
import ru.spbstu.rakitin.dto.TaskType;

@Configuration
@ConfigurationProperties(prefix = "library.monitoring.task")
@Data
public class MonitoringTaskConfiguration {

    private long projectId;
    private String taskName;

    @Bean
    public TaskClientDto taskClientDtoMonitoring() {
        TaskClientDto taskClientDto = new TaskClientDto();
        taskClientDto.setTaskType(TaskType.MONITORING);
        taskClientDto.setProjectId(projectId);
        taskClientDto.setTaskName(taskName);
        return taskClientDto;
    }

    @Bean
    public TaskClientDto taskClientDtoSearchTimeMonitoring() {
        TaskClientDto taskClientDto = new TaskClientDto();
        taskClientDto.setTaskType(TaskType.MONITORING);
        taskClientDto.setProjectId(projectId);
        taskClientDto.setTaskName("library-search-monitoring");
        return taskClientDto;
    }

}
