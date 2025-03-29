package ru.spbstu.rakitin.monitoring_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.spbstu.rakitin.monitoring_service.model.MonitoringTaskConfig;

import java.util.Collection;
import java.util.List;

@Repository
public interface MonitoringTaskConfigRepository extends JpaRepository<MonitoringTaskConfig, Long> {
    boolean existsByNameAndProjectId(String name, Long id);

    int countByProjectId(Long id);

    List<MonitoringTaskConfig> findByProject_IdIn(Collection<Long> projectIds);
}