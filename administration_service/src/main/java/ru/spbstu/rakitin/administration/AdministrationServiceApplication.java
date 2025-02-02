package ru.spbstu.rakitin.administration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import ru.spbstu.rakitin.commonentites.CommonEntitiesAutoConfiguration;
import ru.spbstu.rakitin.commonstarter.DataManagementCommonAutoConfiguration;

@Import({DataManagementCommonAutoConfiguration.class, CommonEntitiesAutoConfiguration.class})
@SpringBootApplication(scanBasePackages = "ru.spbstu.rakitin")
public class AdministrationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdministrationServiceApplication.class, args);
    }

}
