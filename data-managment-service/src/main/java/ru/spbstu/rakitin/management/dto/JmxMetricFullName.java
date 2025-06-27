package ru.spbstu.rakitin.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JmxMetricFullName {

    private final String objectName;
    private final String attribute;

}
