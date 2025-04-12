package ru.spbstu.rakitin.archive_service.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.rakitin.dto.archive.FileDto;
import ru.spbstu.rakitin.dto.archive.FileInformationDto;
import ru.spbstu.rakitin.archive_service.exception.ArchiveTaskInstanceNotFoundException;
import ru.spbstu.rakitin.archive_service.exception.FileNotFoundInArchiveException;
import ru.spbstu.rakitin.archive_service.service.ArchiveTaskQueryService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/archive/query")
@RequiredArgsConstructor
public class ArchiveTaskQueryController {

    private final ArchiveTaskQueryService archiveTaskQueryService;

    @GetMapping("/{configId}/list")
    public List<FileInformationDto> getAllFiles(@PathVariable long configId, Authentication authentication) throws ArchiveTaskInstanceNotFoundException, IOException {
        return archiveTaskQueryService.getAllFilesForTask(configId, authentication);
    }

    @GetMapping("/{configId}/file/{filename}")
    public FileDto getFile(@PathVariable long configId, @PathVariable String filename, Authentication authentication) throws ArchiveTaskInstanceNotFoundException, IOException, FileNotFoundInArchiveException {
        return archiveTaskQueryService.getFile(configId, filename, authentication);
    }



}
