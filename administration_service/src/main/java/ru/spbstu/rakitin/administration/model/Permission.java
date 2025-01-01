package ru.spbstu.rakitin.administration.model;


import jakarta.persistence.*;
import lombok.*;

@Table(schema = "dms", name = "t_user_project_permissions")
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "project_id", referencedColumnName = "id")
    @OneToOne
    private Project project;

    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @OneToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private PermissionTypeEnum permission;

}
