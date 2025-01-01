package ru.spbstu.rakitin.administration.model;

import jakarta.persistence.*;
import lombok.*;

@Table(schema = "dms", name = "t_admin")
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Admin {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String token;

}
