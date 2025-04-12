package ru.spbstu.rakitin.management.engine.processors;

import lombok.Getter;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import ru.spbstu.rakitin.dto.JobDto;
import ru.spbstu.rakitin.dto.MapJson;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.LinkedBlockingQueue;

@Getter
public abstract class AbstractQueueProcessor<Q, T extends JobDto<?>> implements Processor<String, MapJson, String, String> {

    public static final int EXECUTE_PERIOD_SEC = 3;

    private final T jobDto;
    private final String taskName;
    private final LinkedBlockingQueue<Q> queue = new LinkedBlockingQueue<>(100);

    public AbstractQueueProcessor(T jobDto, String taskName) {
        this.jobDto = jobDto;
        this.taskName = taskName;
    }


    @Override
    public void init(ProcessorContext<String, String> context) {
        Processor.super.init(context);
        context.schedule(Duration.of(EXECUTE_PERIOD_SEC, ChronoUnit.SECONDS), PunctuationType.WALL_CLOCK_TIME, timestamp -> {
            try {
                processQueue(queue);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void process(Record<String, MapJson> record) {
        MapJson value = record.value();
        try {
            queue.put(convertJsonToQueueElement(value));
            if (queue.remainingCapacity() == 0) {
                processQueue(queue);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract Q convertJsonToQueueElement(MapJson value);


    protected abstract void processQueue(LinkedBlockingQueue<Q> queue) throws Exception;

}
