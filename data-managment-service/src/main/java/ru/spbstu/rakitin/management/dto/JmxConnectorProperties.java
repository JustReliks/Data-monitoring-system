package ru.spbstu.rakitin.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JmxConnectorProperties {

    private String host;
    private int port;

}
