package ru.spbstu.rakitin.administration.model;

import jakarta.persistence.*;
import lombok.*;

@Table(schema = "dms", name = "t_project")
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String projectName;
    private int fulltextQuota;
    private int archiveQuota;
    private int monitoringQuota;
    private int topicQuota;

}
