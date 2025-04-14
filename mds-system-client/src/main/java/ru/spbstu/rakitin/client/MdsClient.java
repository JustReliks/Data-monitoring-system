package ru.spbstu.rakitin.client;

import lombok.SneakyThrows;
import ru.spbstu.rakitin.dto.TaskClientDto;

public interface MdsClient {
    @SneakyThrows
    <T, R> MdsResponse<R> sendRequest(MdsRequest<T, R> request);

    <T> void sendMessageToTask(TaskClientDto task, T data);
}
