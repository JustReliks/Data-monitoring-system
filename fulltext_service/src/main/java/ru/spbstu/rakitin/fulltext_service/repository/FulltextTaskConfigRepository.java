package ru.spbstu.rakitin.fulltext_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.spbstu.rakitin.fulltext_service.model.FulltextTaskConfig;

import java.util.Collection;
import java.util.List;

@Repository
public interface FulltextTaskConfigRepository extends JpaRepository<FulltextTaskConfig, Long> {

    boolean existsByNameAndProjectId(String name, long projectId);
    int countByProjectId(long projectId);

    List<FulltextTaskConfig> findAllByProject_IdIn(Collection<Long> projectIds);
}
