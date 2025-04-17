package ru.spbstu.rakitin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.spbstu.rakitin.model.MonitoringToken;

import java.util.Optional;

@Repository
public interface MonitoringTokenRepository extends JpaRepository<MonitoringToken, Long> {

    Optional<MonitoringToken> findFirstByOrderByCreatedAtDesc();

}
