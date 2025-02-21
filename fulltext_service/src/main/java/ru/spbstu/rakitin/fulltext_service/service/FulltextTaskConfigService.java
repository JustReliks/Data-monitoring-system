package ru.spbstu.rakitin.fulltext_service.service;

import org.springframework.security.core.Authentication;
import ru.spbstu.rakitin.commonstarter.exception.InvalidSchemaException;
import ru.spbstu.rakitin.fulltext_service.exception.*;
import ru.spbstu.rakitin.fulltext_service.model.FulltextTaskConfig;

public interface FulltextTaskConfigService {

    void createConfig(FulltextTaskConfig config, Authentication authentication) throws ConfigAlreadyExists, FulltextQuotaExceededException, UnavailableTopicException, InvalidSchemaException;

    FulltextTaskConfig findById(long id) throws FulltextConfigNotFoundException;

}
