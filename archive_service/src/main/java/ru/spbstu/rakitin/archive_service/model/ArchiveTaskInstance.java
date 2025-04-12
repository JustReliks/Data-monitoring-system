package ru.spbstu.rakitin.archive_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.spbstu.rakitin.dto.TaskStatus;

@Getter
@Setter
@Entity
@Table(name = "t_archive_task_instance", schema = "dms")
public class ArchiveTaskInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "config_id", nullable = false)
    private ArchiveTaskConfig config;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TaskStatus status;

    private boolean needUpdate;

}