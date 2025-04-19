package ru.spbstu.rakitin.fulltext_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.dto.PermissionTypeEnum;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.dto.MapJson;
import ru.spbstu.rakitin.dto.fulltext.SolrQueryDto;
import ru.spbstu.rakitin.fulltext_service.engine.SolrClientManager;
import ru.spbstu.rakitin.fulltext_service.exception.FulltextConfigNotFoundException;
import ru.spbstu.rakitin.fulltext_service.exception.FulltextTaskInstanceNotFoundException;
import ru.spbstu.rakitin.fulltext_service.exception.FulltextTaskInstanceNotRunningException;
import ru.spbstu.rakitin.fulltext_service.model.FulltextTaskConfig;
import ru.spbstu.rakitin.fulltext_service.model.FulltextTaskInstance;
import ru.spbstu.rakitin.dto.TaskStatus;
import ru.spbstu.rakitin.fulltext_service.service.FulltextTaskConfigService;
import ru.spbstu.rakitin.fulltext_service.service.FulltextTaskInstanceService;
import ru.spbstu.rakitin.fulltext_service.service.FulltextTaskService;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FulltextTaskServiceImpl implements FulltextTaskService {

    private final FulltextTaskConfigService fulltextTaskConfigService;
    private final FulltextTaskInstanceService fulltextTaskInstanceService;
    private final SolrClientManager solrClientManager;
    private final AdminManager adminManager;

    @Override
    public List<MapJson> query(SolrQueryDto query, long taskId, Authentication authentication) throws FulltextConfigNotFoundException, FulltextTaskInstanceNotFoundException, FulltextTaskInstanceNotRunningException, SolrServerException, IOException {
        FulltextTaskConfig config = fulltextTaskConfigService.findById(taskId, authentication);
        adminManager.checkAccessThrowable(authentication, config.getProject().getId(), PermissionTypeEnum.FULL_TEXT_VIEW_TASK);
        FulltextTaskInstance instance = fulltextTaskInstanceService.findByConfigId(taskId);
        if (instance.getTaskStatus() == TaskStatus.INITIATION_FAILED || instance.getTaskStatus() == TaskStatus.CREATED) {
            throw new FulltextTaskInstanceNotRunningException("Instance with id %s was not initiated yet. Can't fetch data!");
        }

        return solrClientManager.query(config, query);
    }
}
