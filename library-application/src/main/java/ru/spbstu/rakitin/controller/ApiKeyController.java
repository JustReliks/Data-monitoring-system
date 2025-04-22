package ru.spbstu.rakitin.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.rakitin.service.MonitoringTokenService;

@RestController
@RequestMapping("/api/v1/key")
public class ApiKeyController {

    private final MonitoringTokenService monitoringTokenService;

    public ApiKeyController(MonitoringTokenService monitoringTokenService) {
        this.monitoringTokenService = monitoringTokenService;
    }

    @PostMapping("/refresh")
    public String refreshApiKey() {
       return monitoringTokenService.refresh();
    }

}
