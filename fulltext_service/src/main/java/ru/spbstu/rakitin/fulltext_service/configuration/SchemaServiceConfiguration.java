package ru.spbstu.rakitin.fulltext_service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.spbstu.rakitin.commonstarter.dto.TaskSchemaDto;
import ru.spbstu.rakitin.commonstarter.service.SchemaValidationService;
import ru.spbstu.rakitin.commonstarter.service.impl.SchemaValidationServiceImpl;

@Configuration
public class SchemaServiceConfiguration {

    @Bean
    public SchemaValidationService<TaskSchemaDto> schemaValidationService() {
        return new SchemaValidationServiceImpl<>();
    }

}
