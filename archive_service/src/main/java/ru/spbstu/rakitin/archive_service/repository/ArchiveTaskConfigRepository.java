package ru.spbstu.rakitin.archive_service.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.spbstu.rakitin.archive_service.model.ArchiveTaskConfig;

import java.util.Collection;
import java.util.List;

@Repository
public interface ArchiveTaskConfigRepository extends JpaRepository<ArchiveTaskConfig, Long> {
    boolean existsByNameAndProjectId(@NotNull String name, Long projectId);

    int countByProjectId(Long projectId);

    List<ArchiveTaskConfig> findByProject_IdIn(Collection<Long> projectIds);
}