package ru.spbstu.rakitin.management.engine;

import ru.spbstu.rakitin.commonstarter.dto.JobDto;
import ru.spbstu.rakitin.commonstarter.utils.MapJson;
import ru.spbstu.rakitin.management.engine.processors.AbstractQueueProcessor;

public abstract class AbstractJsonQueueProcessor<T extends JobDto<?>> extends AbstractQueueProcessor<MapJson, T> {
    public AbstractJsonQueueProcessor(T jobDto, String taskName) {
        super(jobDto, taskName);
    }

    @Override
    protected MapJson convertJsonToQueueElement(MapJson value) {
        return value;
    }
}
