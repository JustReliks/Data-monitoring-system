package ru.spbstu.rakitin.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Entity
@Table(schema = "lib", name = "t_monitoring_token")
@Getter
@Setter
@ToString
public class MonitoringToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;
    private Date createdAt;

}
