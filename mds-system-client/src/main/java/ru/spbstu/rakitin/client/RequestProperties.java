package ru.spbstu.rakitin.client;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestProperties {

    private long retryDelayMs;
    private long retryCount;
    public int threadsCount;
    private String baseUrl;
}
