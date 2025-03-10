package ru.spbstu.rakitin.archive_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.spbstu.rakitin.commonentites.model.Project;
import ru.spbstu.rakitin.commonentites.model.Topic;

@Getter
@Setter
@Entity
@Table(name = "t_archive_task_config", schema = "dms")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArchiveTaskConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "schema", nullable = false, length = Integer.MAX_VALUE)
    @Convert(converter = SchemaConverter.class)
    private ArchiveTaskSchema schema;

    @NotNull
    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

}