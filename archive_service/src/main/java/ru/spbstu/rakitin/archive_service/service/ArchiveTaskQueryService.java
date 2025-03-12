package ru.spbstu.rakitin.archive_service.service;

import org.springframework.security.core.Authentication;
import ru.spbstu.rakitin.archive_service.dto.FileInformationDto;
import ru.spbstu.rakitin.archive_service.exception.ArchiveTaskInstanceNotFoundException;

import java.io.IOException;
import java.util.List;

public interface ArchiveTaskQueryService {

    List<FileInformationDto> getAllFilesForTask(long taskId, Authentication authentication) throws ArchiveTaskInstanceNotFoundException, IOException;

}
