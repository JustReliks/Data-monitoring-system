package ru.spbstu.rakitin.client;

import ru.spbstu.rakitin.dto.TaskClientDto;
import ru.spbstu.rakitin.dto.TaskInformator;

public interface TaskResolver {
    String resolveTopicName(TaskClientDto taskClientDto);

    TaskInformator resolveTaskInformation(TaskClientDto taskClientDto);

    long resolveTaskId(TaskClientDto taskClientDto);
}
