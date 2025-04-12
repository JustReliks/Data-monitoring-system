package ru.spbstu.rakitin.fulltext_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.spbstu.rakitin.fulltext_service.model.FulltextTaskInstance;
import ru.spbstu.rakitin.dto.TaskStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface FulltextTaskInstanceRepository extends JpaRepository<FulltextTaskInstance, Long> {

    Optional<FulltextTaskInstance> findByConfigId(long configId);
    List<FulltextTaskInstance> findAllByTaskStatus(TaskStatus taskStatus);


}
