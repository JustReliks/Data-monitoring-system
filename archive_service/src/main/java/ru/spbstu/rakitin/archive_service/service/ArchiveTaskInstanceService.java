package ru.spbstu.rakitin.archive_service.service;

import org.springframework.security.core.Authentication;

public interface ArchiveTaskInstanceService {

    long resume(long configId, Authentication authentication) throws Exception;

}
