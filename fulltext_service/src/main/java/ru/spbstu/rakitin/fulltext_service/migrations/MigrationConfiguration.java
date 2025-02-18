package ru.spbstu.rakitin.fulltext_service.migrations;

import lombok.RequiredArgsConstructor;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MigrationConfiguration implements FlywayConfigurationCustomizer {

    private final List<BaseJavaMigration> migrations;


    @Override
    public void customize(FluentConfiguration configuration) {
        configuration.javaMigrations(migrations.toArray(migrations.toArray(new BaseJavaMigration[0])));
    }
}
