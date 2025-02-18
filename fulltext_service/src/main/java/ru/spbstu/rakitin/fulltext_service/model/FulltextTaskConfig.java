package ru.spbstu.rakitin.fulltext_service.model;

import jakarta.persistence.*;
import lombok.*;
import ru.spbstu.rakitin.commonentites.model.Project;
import ru.spbstu.rakitin.commonentites.model.Topic;

@Entity
@Table(schema = "dms", name = "t_fulltext_task_config")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FulltextTaskConfig {

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
    private FulltextTaskSchema schema;

    private int replicationFactor;
    private int shardsCount;
}
