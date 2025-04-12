package ru.spbstu.rakitin.fulltext_service.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.security.core.Authentication;
import ru.spbstu.rakitin.dto.TaskStatus;
import ru.spbstu.rakitin.commonstarter.exception.InstanceInitiationFailedException;
import ru.spbstu.rakitin.fulltext_service.exception.*;
import ru.spbstu.rakitin.fulltext_service.model.FulltextTaskInstance;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface FulltextTaskInstanceService {

    void suspendTask(long taskId, Authentication authentication) throws FulltextTaskInstanceNotFoundException, FulltextStatusWontChangedException;

    long resume(long configId, Authentication authentication) throws FulltextConfigNotFoundException, IllegalAccessException, FulltextTaskInstanceAlreadyRunningException, InstanceInitiationFailedException, FulltextTaskInstanceResumeException;

    void forceChangeFulltextInstanceStatus(long instanceId, TaskStatus taskStatus) throws FulltextTaskInstanceNotFoundException, FulltextStatusWontChangedException;

    void update(long configId, Authentication authentication) throws Exception;

    List<FulltextTaskInstance> findAllTaskInstancesWithStatus(TaskStatus taskStatus);

    FulltextTaskInstance findByConfigId(long configId) throws FulltextTaskInstanceNotFoundException;

    Optional<FulltextTaskInstance> findByConfigIdOptionally(long configId);

    FulltextTaskInstance findById(long id) throws FulltextTaskInstanceNotFoundException;

    void removeInstance(Long id, Authentication authentication) throws FulltextTaskInstanceNotFoundException, FulltextStatusWontChangedException, SolrServerException, IOException;


    void update(FulltextTaskInstance taskInstance) throws FulltextTaskInstanceNotFoundException;
}

