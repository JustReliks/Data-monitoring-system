package ru.spbstu.rakitin.management.api.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.rakitin.commonstarter.admin.aspect.LogController;
import ru.spbstu.rakitin.commonstarter.dto.JobNameDto;
import ru.spbstu.rakitin.commonstarter.dto.monitoring.MonitoringJobDto;
import ru.spbstu.rakitin.management.service.impl.MonitoringJobService;

@RestController
@RequestMapping("/api/v1/job/monitoring")
@RequiredArgsConstructor
public class InternalMonitoringJobController {

    private final MonitoringJobService jobService;

    @PostMapping("/start")
    @LogController
    public void startJob(@RequestBody MonitoringJobDto jobDto, Authentication authentication) throws Exception {
        jobService.startJob(jobDto);
    }

    @PostMapping("/stop")
    @LogController
    public void stopJob(@RequestBody JobNameDto taskName, Authentication authentication) throws Exception {
        jobService.stopJob(taskName);
    }

}
