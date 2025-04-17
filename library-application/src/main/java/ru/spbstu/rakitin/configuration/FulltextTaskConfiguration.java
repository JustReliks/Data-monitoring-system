package ru.spbstu.rakitin.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.spbstu.rakitin.dto.TaskClientDto;
import ru.spbstu.rakitin.dto.TaskType;

@Configuration
@ConfigurationProperties(prefix = "library.fulltext.task")
@Data
public class FulltextTaskConfiguration {

    private long projectId;
    private String taskName;

    @Bean
    public TaskClientDto taskClientDtoFulltext() {
        TaskClientDto taskClientDto = new TaskClientDto();
        taskClientDto.setTaskType(TaskType.FULLTEXT);
        taskClientDto.setProjectId(projectId);
        taskClientDto.setTaskName(taskName);
        return taskClientDto;
    }


}
