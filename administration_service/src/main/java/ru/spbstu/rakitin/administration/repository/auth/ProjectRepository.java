package ru.spbstu.rakitin.administration.repository.auth;

import ru.spbstu.rakitin.commonentites.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findProjectByProjectName(String projectName);

}
