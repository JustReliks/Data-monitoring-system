package ru.spbstu.rakitin.monitoring_service.model;

import jakarta.persistence.*;
import lombok.*;
import ru.spbstu.rakitin.commonentites.model.Project;
import ru.spbstu.rakitin.commonentites.model.Topic;

@Entity
@Table(schema = "dms", name = "t_monitoring_task_config")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringTaskConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @JoinColumn(name = "project_id", referencedColumnName = "id")
    @ManyToOne
    private Project project;

    @JoinColumn(name = "topic_id", referencedColumnName = "id")
    @ManyToOne
    private Topic topic;

    @Column(name = "schema")
    @Convert(converter = SchemaConverter.class)
    private MonitoringTaskSchema schema;

    @Column(name = "retention_time_sec", nullable = false)
    private int retentionTimeSeconds;

    @Column(name = "shard_group_duration_sec", nullable = false)
    private long shardGroupDurationSeconds;

}
