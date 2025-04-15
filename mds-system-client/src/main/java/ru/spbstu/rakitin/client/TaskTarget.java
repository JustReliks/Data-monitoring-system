package ru.spbstu.rakitin.client;

import ru.spbstu.rakitin.dto.TaskClientDto;
import ru.spbstu.rakitin.dto.TaskInformator;

public interface TaskTarget {

    void setTask(TaskInformator taskInformator);

    TaskInformator getTask();

    TaskClientDto getTaskClientDto();

}
