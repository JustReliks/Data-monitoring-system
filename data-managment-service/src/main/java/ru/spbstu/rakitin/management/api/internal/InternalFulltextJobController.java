package ru.spbstu.rakitin.management.api.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.rakitin.commonstarter.admin.aspect.LogController;
import ru.spbstu.rakitin.commonstarter.dto.JobNameDto;
import ru.spbstu.rakitin.commonstarter.dto.fulltext.FulltextJobDto;
import ru.spbstu.rakitin.management.service.impl.FulltextJobService;

@RestController
@RequestMapping("/api/v1/job/fulltext")
@RequiredArgsConstructor
public class InternalFulltextJobController {

    private final FulltextJobService jobService;

    @PostMapping("/start")
    @LogController
    public void startJob(@RequestBody FulltextJobDto jobDto, Authentication authentication) throws Exception {
        jobService.startJob(jobDto);
    }

    @PostMapping("/stop")
    @LogController
    public void stopJob(@RequestBody JobNameDto taskName, Authentication authentication) throws Exception {
        jobService.stopJob(taskName);
    }

}
