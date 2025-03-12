package ru.spbstu.rakitin.archive_service.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.spbstu.rakitin.archive_service.model.ArchiveTaskInstance;
import ru.spbstu.rakitin.commonstarter.dto.TaskStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArchiveTaskInstanceRepository extends JpaRepository<ArchiveTaskInstance, Long> {

    Optional<ArchiveTaskInstance> findArchiveTaskInstanceByConfigId(long configId);

    List<ArchiveTaskInstance> findAllByStatus(@NotNull TaskStatus status);
}