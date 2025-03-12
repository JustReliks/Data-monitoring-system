package ru.spbstu.rakitin.archive_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.archive_service.dto.FileInformationDto;
import ru.spbstu.rakitin.archive_service.engine.HdfsManager;
import ru.spbstu.rakitin.archive_service.exception.ArchiveTaskInstanceNotFoundException;
import ru.spbstu.rakitin.archive_service.model.ArchiveTaskConfig;
import ru.spbstu.rakitin.archive_service.model.ArchiveTaskInstance;
import ru.spbstu.rakitin.archive_service.service.ArchiveTaskInstanceService;
import ru.spbstu.rakitin.archive_service.service.ArchiveTaskQueryService;
import ru.spbstu.rakitin.commonentites.model.PermissionTypeEnum;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.commonstarter.dto.TaskStatus;

import java.io.IOException;
import java.util.List;

import static ru.spbstu.rakitin.archive_service.dto.ArchiveTaskConfigMapper.getJobFolderName;

@Service
@RequiredArgsConstructor
public class ArchiveTaskQueryServiceImpl implements ArchiveTaskQueryService {

    private final ArchiveTaskInstanceService archiveTaskInstanceService;
    private final AdminManager adminManager;
    private final HdfsManager hdfsManager;

    @Override
    public List<FileInformationDto> getAllFilesForTask(long configId, Authentication authentication) throws ArchiveTaskInstanceNotFoundException, IOException {
        ArchiveTaskInstance archiveTaskInstance = archiveTaskInstanceService.findByConfigId(configId);
        if (archiveTaskInstance.getStatus() != TaskStatus.RUNNING) {
            throw new ArchiveTaskInstanceNotFoundException(String.format("Running archive task instance for task with id %s not found", configId));
        }
        ArchiveTaskConfig config = archiveTaskInstance.getConfig();

        adminManager.checkAccessThrowable(authentication, config.getProject().getId(), PermissionTypeEnum.ARCHIVE_VIEW_TASK);

        return hdfsManager.getAllFilesInDirectory(getJobFolderName(archiveTaskInstance));
    }
}
