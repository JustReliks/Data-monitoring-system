package ru.spbstu.rakitin.archive_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.spbstu.rakitin.archive_service.model.ArchiveTaskInstance;

import java.util.Optional;

@Repository
public interface ArchiveTaskInstanceRepository extends JpaRepository<ArchiveTaskInstance, Integer> {

    Optional<ArchiveTaskInstance> findArchiveTaskInstanceByConfigId(long configId);

}