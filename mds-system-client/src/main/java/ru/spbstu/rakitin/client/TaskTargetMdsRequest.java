package ru.spbstu.rakitin.client;

import lombok.RequiredArgsConstructor;
import ru.spbstu.rakitin.dto.TaskClientDto;
import ru.spbstu.rakitin.dto.TaskInformator;

@RequiredArgsConstructor
public abstract class TaskTargetMdsRequest<T, R> extends MdsRequest<T, R> implements TaskTarget {

    private TaskInformator taskInformator;
    private final TaskClientDto taskClientDto;

    @Override
    public TaskInformator getTask() {
        return taskInformator;
    }

    @Override
    public void setTask(TaskInformator taskInformator) {
        this.taskInformator = taskInformator;
    }

    @Override
    public TaskClientDto getTaskClientDto() {
        return taskClientDto;
    }
}
