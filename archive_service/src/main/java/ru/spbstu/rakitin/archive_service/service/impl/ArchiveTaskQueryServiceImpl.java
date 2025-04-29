package ru.spbstu.rakitin.archive_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.archive_service.engine.HdfsManager;
import ru.spbstu.rakitin.archive_service.exception.ArchiveTaskInstanceNotFoundException;
import ru.spbstu.rakitin.archive_service.exception.FileNotFoundInArchiveException;
import ru.spbstu.rakitin.archive_service.model.ArchiveTaskConfig;
import ru.spbstu.rakitin.archive_service.model.ArchiveTaskInstance;
import ru.spbstu.rakitin.archive_service.service.ArchiveTaskInstanceService;
import ru.spbstu.rakitin.archive_service.service.ArchiveTaskQueryService;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.dto.PermissionTypeEnum;
import ru.spbstu.rakitin.dto.TaskStatus;
import ru.spbstu.rakitin.dto.archive.FileDto;
import ru.spbstu.rakitin.dto.archive.FileInformationDto;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static ru.spbstu.rakitin.archive_service.dto.ArchiveTaskConfigMapper.getJobFolderName;

@Service
@RequiredArgsConstructor
public class ArchiveTaskQueryServiceImpl implements ArchiveTaskQueryService {

    private final ArchiveTaskInstanceService archiveTaskInstanceService;
    private final AdminManager adminManager;
    private final HdfsManager hdfsManager;

    @Override
    public List<FileInformationDto> getAllFilesForTask(long configId, String directory, Authentication authentication) throws ArchiveTaskInstanceNotFoundException, IOException {
        ArchiveTaskInstance archiveTaskInstance = getArchiveTaskInstance(configId, authentication);

        String jobFolderName = getJobFolderName(archiveTaskInstance);

        return hdfsManager.getAllFilesInDirectory(jobFolderName, StringUtils.isNotEmpty(directory) ? Optional.of(directory) : Optional.empty());
    }

    @Override
    public FileDto getFile(long configId, String filename, String directory, Authentication authentication) throws ArchiveTaskInstanceNotFoundException, IOException, FileNotFoundInArchiveException {
        ArchiveTaskInstance archiveTaskInstance = getArchiveTaskInstance(configId, authentication);

        String path = getJobFolderName(archiveTaskInstance);
        if (!StringUtils.isEmpty(directory)) {
            path += "/" + directory;
        }
        return hdfsManager.getFile(path + "/" + filename + ".json");

    }

    private ArchiveTaskInstance getArchiveTaskInstance(long configId, Authentication authentication) throws ArchiveTaskInstanceNotFoundException {
        ArchiveTaskInstance archiveTaskInstance = archiveTaskInstanceService.findByConfigId(configId);
        if (archiveTaskInstance.getStatus() != TaskStatus.RUNNING) {
            throw new ArchiveTaskInstanceNotFoundException(String.format("Running archive task instance for task with id %s not found", configId));
        }
        ArchiveTaskConfig config = archiveTaskInstance.getConfig();

        adminManager.checkAccessThrowable(authentication, config.getProject().getId(), PermissionTypeEnum.ARCHIVE_VIEW_TASK);
        return archiveTaskInstance;
    }
}
