package ru.spbstu.rakitin.fulltext_service.model;

import jakarta.persistence.*;
import lombok.*;
import ru.spbstu.rakitin.dto.TaskStatus;

@Entity
@Table(schema = "dms", name = "t_fulltext_task_instance")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FulltextTaskInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "config_id")
    private FulltextTaskConfig config;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskStatus taskStatus;

    private boolean needUpdate;

}
