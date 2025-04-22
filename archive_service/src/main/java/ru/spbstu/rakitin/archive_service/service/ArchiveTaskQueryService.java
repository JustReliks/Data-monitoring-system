package ru.spbstu.rakitin.archive_service.service;

import org.springframework.security.core.Authentication;
import ru.spbstu.rakitin.archive_service.exception.ArchiveTaskInstanceNotFoundException;
import ru.spbstu.rakitin.archive_service.exception.FileNotFoundInArchiveException;
import ru.spbstu.rakitin.dto.archive.FileDto;
import ru.spbstu.rakitin.dto.archive.FileInformationDto;

import java.io.IOException;
import java.util.List;

public interface ArchiveTaskQueryService {

    List<FileInformationDto> getAllFilesForTask(long taskId, String directory, Authentication authentication) throws ArchiveTaskInstanceNotFoundException, IOException;

    FileDto getFile(long taskId, String filename, String directory, Authentication authentication) throws ArchiveTaskInstanceNotFoundException, IOException, FileNotFoundInArchiveException;
}
