package ru.spbstu.rakitin.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.spbstu.rakitin.commonentites.model.Topic;

import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    Optional<Topic> findByName(String name);
    Optional<Topic> findByNameInKafka(String nameInKafka);
    int countAllByProject_Id(Long projectId);

}
