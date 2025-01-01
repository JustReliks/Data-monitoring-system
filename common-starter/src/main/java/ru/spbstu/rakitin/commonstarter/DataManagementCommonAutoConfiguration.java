package ru.spbstu.rakitin.commonstarter;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.spbstu.rakitin.commonstarter.admin.AdminRequestConfiguration;

@EnableAutoConfiguration
@AutoConfiguration
@EnableConfigurationProperties({AdminRequestConfiguration.class})
public class DataManagementCommonAutoConfiguration {
}
