package ru.spbstu.rakitin.user_api_service.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.commonstarter.archive.ArchiveServiceManager;
import ru.spbstu.rakitin.commonstarter.datamanagement.DataManagementManager;
import ru.spbstu.rakitin.commonstarter.discovery.DiscoveryService;
import ru.spbstu.rakitin.commonstarter.fulltext.FulltextServiceManager;
import ru.spbstu.rakitin.commonstarter.monitoring.MonitoringServiceManager;
import ru.spbstu.rakitin.dto.*;
import ru.spbstu.rakitin.user_api_service.service.UserAccessesService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserAccessesServiceImpl implements UserAccessesService {

    private final FulltextServiceManager fulltextServiceManager;
    private final ArchiveServiceManager archiveServiceManager;
    private final MonitoringServiceManager monitoringServiceManager;
    private final DiscoveryService discoveryService;
    private final DataManagementManager dataManagementManager;
    private final AdminManager adminManager;

    public UserAccessesServiceImpl(FulltextServiceManager fulltextServiceManager, ArchiveServiceManager archiveServiceManager, MonitoringServiceManager monitoringServiceManager, DiscoveryService discoveryService, DataManagementManager dataManagementManager, AdminManager adminManager) {
        this.fulltextServiceManager = fulltextServiceManager;
        this.archiveServiceManager = archiveServiceManager;
        this.monitoringServiceManager = monitoringServiceManager;
        this.discoveryService = discoveryService;
        this.dataManagementManager = dataManagementManager;
        this.adminManager = adminManager;
    }


    @Override
    public List<UserTaskAccessDto> accessList(Authentication authentication) {
        List<UserProjectAccessDto> allProjectsAvailable = adminManager.findAllProjectsAvailable(authentication);
        List<Long> archiveProjects = new ArrayList<>();
        List<Long> monitoringProjects = new ArrayList<>();
        List<Long> fulltextProjects = new ArrayList<>();
        List<Long> topicProjects = new ArrayList<>();

        for (UserProjectAccessDto userProjectAccessDto : allProjectsAvailable) {
            if (adminManager.canUserDoActionInProject(authentication, userProjectAccessDto.getProject().getId(), PermissionTypeEnum.ARCHIVE_VIEW_TASK)) {
                archiveProjects.add(userProjectAccessDto.getProject().getId());
            }
            if (adminManager.canUserDoActionInProject(authentication, userProjectAccessDto.getProject().getId(), PermissionTypeEnum.MONITORING_VIEW_TASK)) {
                monitoringProjects.add(userProjectAccessDto.getProject().getId());
            }
            if (adminManager.canUserDoActionInProject(authentication, userProjectAccessDto.getProject().getId(), PermissionTypeEnum.FULL_TEXT_VIEW_TASK)) {
                fulltextProjects.add(userProjectAccessDto.getProject().getId());
            }
            if (adminManager.canUserDoActionInProject(authentication, userProjectAccessDto.getProject().getId(), PermissionTypeEnum.TOPIC_VIEW)) {
                topicProjects.add(userProjectAccessDto.getProject().getId());
            }
        }
        List<TaskInformator> fulltextList = List.of();
        List<TaskInformator> monitoringList = List.of();
        List<TaskInformator> archiveList = List.of();
        List<LightWeightTopicDto> topics = List.of();
        if (discoveryService.isServiceAvailable(ServiceName.DATA_MANAGEMENT)) {
            topics = topicProjects.stream().flatMap(id -> dataManagementManager.getAllTopicsForProjectId(id, authentication).stream())
                    .toList();
        }
        if (discoveryService.isServiceAvailable(ServiceName.ARCHIVE)) {
            archiveList = archiveServiceManager.list(archiveProjects, authentication)
                    .stream().map(TaskInformator.class::cast).toList();
        }
        if (discoveryService.isServiceAvailable(ServiceName.MONITORING)) {
            monitoringList = monitoringServiceManager.list(monitoringProjects, authentication)
                    .stream().map(TaskInformator.class::cast).toList();
        }
        if (discoveryService.isServiceAvailable(ServiceName.FULL_TEXT)) {
            fulltextList = fulltextServiceManager.list(fulltextProjects, authentication)
                    .stream().map(TaskInformator.class::cast).toList();
        }
        List<TaskInformator> taskInformators = new ArrayList<>();
        taskInformators.addAll(fulltextList);
        taskInformators.addAll(archiveList);
        taskInformators.addAll(monitoringList);

        List<LightWeightTopicDto> finalTopics = topics;
        return allProjectsAvailable.stream().map(userProjectAccessDto -> {
            UserTaskAccessDto userTaskAccessDto = new UserTaskAccessDto();
            userTaskAccessDto.setProjectAccess(userProjectAccessDto);
            userTaskAccessDto.setTasks(taskInformators.stream().filter(taskInformator -> Objects.equals(userProjectAccessDto.getProject().getId(), taskInformator.getProjectId())).toList());
            userTaskAccessDto.setTopics(finalTopics.stream().filter(lightWeightTopicDto -> Objects.equals(lightWeightTopicDto.getProjectId(), userProjectAccessDto.getProject().getId())).toList());
            return userTaskAccessDto;
        }).toList();
    }

}
