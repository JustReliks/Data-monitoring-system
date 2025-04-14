package ru.spbstu.rakitin.client;

import ru.spbstu.rakitin.dto.TaskClientDto;

public interface TaskResolver {
    String resolveTopicName(TaskClientDto taskClientDto);
}
