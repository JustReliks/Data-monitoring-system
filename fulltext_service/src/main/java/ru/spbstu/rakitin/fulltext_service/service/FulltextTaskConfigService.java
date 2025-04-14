package ru.spbstu.rakitin.fulltext_service.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.security.core.Authentication;
import ru.spbstu.rakitin.commonstarter.exception.ConfigAlreadyExists;
import ru.spbstu.rakitin.commonstarter.exception.InvalidSchemaException;
import ru.spbstu.rakitin.commonstarter.exception.QuotaExceededException;
import ru.spbstu.rakitin.commonstarter.exception.UnavailableTopicException;
import ru.spbstu.rakitin.fulltext_service.exception.*;
import ru.spbstu.rakitin.fulltext_service.model.FulltextTaskConfig;

import java.io.IOException;
import java.util.List;

public interface FulltextTaskConfigService {

    long createConfig(FulltextTaskConfig config, Authentication authentication) throws ConfigAlreadyExists, QuotaExceededException, UnavailableTopicException, InvalidSchemaException;

    FulltextTaskConfig findById(long id, Authentication authentication) throws FulltextConfigNotFoundException;

    long saveConfig(FulltextTaskConfig config) throws ConfigAlreadyExists, QuotaExceededException, UnavailableTopicException, InvalidSchemaException;

    List<FulltextTaskConfig> findForProjects(List<Long> projectIds, Authentication authentication);

    void removeConfig(Long configId, boolean forceDelete, Authentication authentication) throws FulltextConfigNotFoundException, FulltextConfigDeletionForbiddenException, FulltextStatusWontChangedException, FulltextTaskInstanceNotFoundException, SolrServerException, IOException;

    void updateConfig(long configId, FulltextTaskConfig fulltextTaskConfig, Authentication authentication) throws FulltextConfigNotFoundException, FulltextConfigUpdateException, QuotaExceededException, InvalidSchemaException, ConfigAlreadyExists, UnavailableTopicException;

    FulltextTaskConfig findByName(Long projectId, String taskName, Authentication authentication) throws FulltextConfigNotFoundException;
}
