package ru.spbstu.rakitin.monitoring_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.spbstu.rakitin.dto.TaskStatus;
import ru.spbstu.rakitin.monitoring_service.model.MonitoringTaskInstance;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonitoringTaskInstanceRepository extends JpaRepository<MonitoringTaskInstance, Long> {
    Optional<MonitoringTaskInstance> findFirstByConfigId(Long configId);

    List<MonitoringTaskInstance> findAllByTaskStatus(TaskStatus taskStatus);

    List<MonitoringTaskInstance> findByConfigIdIn(List<Long> ids);
}