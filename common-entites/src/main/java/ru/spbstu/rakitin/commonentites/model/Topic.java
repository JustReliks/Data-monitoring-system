package ru.spbstu.rakitin.commonentites.model;

import jakarta.persistence.*;
import lombok.*;

@Table(schema = "dms", name = "t_topic")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @JoinColumn(name = "project_id", referencedColumnName = "id")
    @ManyToOne
    private Project project;

    private int partitions;

    private int replicationFactor;

    private String uuid;

    private String nameInKafka;

}
