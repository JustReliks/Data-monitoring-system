package ru.spbstu.rakitin.commonstarter.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.spbstu.rakitin.dto.TaskSchemaDto;
import ru.spbstu.rakitin.commonstarter.service.SchemaValidationService;
import ru.spbstu.rakitin.commonstarter.service.impl.SchemaValidationServiceImpl;

@Configuration
public class SchemaServiceConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SchemaValidationService<TaskSchemaDto> schemaValidationService() {
        return new SchemaValidationServiceImpl<>();
    }

}
