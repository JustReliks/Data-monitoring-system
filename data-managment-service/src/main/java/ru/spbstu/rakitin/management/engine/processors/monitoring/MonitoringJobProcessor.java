package ru.spbstu.rakitin.management.engine.processors.monitoring;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import lombok.extern.slf4j.Slf4j;
import ru.spbstu.rakitin.commonstarter.dto.FieldType;
import ru.spbstu.rakitin.commonstarter.dto.SchemaFieldDto;
import ru.spbstu.rakitin.commonstarter.dto.monitoring.MonitoringJobDto;
import ru.spbstu.rakitin.commonstarter.utils.MapJson;
import ru.spbstu.rakitin.management.engine.influxdb.InfluxDbClientFactory;
import ru.spbstu.rakitin.management.engine.processors.AbstractQueueProcessor;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class MonitoringJobProcessor extends AbstractQueueProcessor<Point, MonitoringJobDto> {

    private final InfluxDbClientFactory influxDbClientFactory;
    private final InfluxDBClient influxDBClient;
    private final WriteApiBlocking writeApiBlocking;

    public MonitoringJobProcessor(MonitoringJobDto jobDto, String taskName, InfluxDbClientFactory influxDbClientFactory) {
        super(jobDto, taskName);
        this.influxDbClientFactory = influxDbClientFactory;
        this.influxDBClient = influxDbClientFactory.getInfluxDbClient(jobDto.getTaskName(), jobDto.getOrganization());
        writeApiBlocking = influxDBClient.getWriteApiBlocking();
    }

    @Override
    public void close() {
        super.close();
        influxDbClientFactory.close(getTaskName());
    }

    @Override
    protected Point convertJsonToQueueElement(MapJson value) {
        Point point = Point.measurement(getJobDto().getTaskName());
        value.forEach((k, v) -> {
            if (k.equals(getJobDto().getSchema().getTimestampField().getFieldName())) {
                point.time(DateTimeFormatter.ISO_INSTANT.parse(value.get(getJobDto().getSchema().getTimestampField().getFieldName()).toString()).getLong(ChronoField.INSTANT_SECONDS), WritePrecision.S);

            } else {
                SchemaFieldDto schemaFieldDto = getJobDto().getSchema().getField(k).get();
                if (schemaFieldDto.getFieldType().isCompatibleWith(FieldType.STRING)) {
                    point.addTag(k, v.toString());
                } else if (schemaFieldDto.getFieldType().isCompatibleWith(FieldType.DOUBLE)) {
                    point.addField(k, Double.valueOf(v.toString()));
                }
            }
        });
        return point;
    }

    @Override
    protected void processQueue(LinkedBlockingQueue<Point> queue) throws Exception {
        if (!queue.isEmpty()) {
            log.info("Processing queue in monitoring job. Sending {} points to {} in organization {}", queue.size(), getTaskName(), getJobDto().getOrganization());
            List<Point> pointList = new ArrayList<>();
            queue.drainTo(pointList);
            writeApiBlocking.writePoints(pointList);
            log.info("Successfully sent {} points to {} in organization {}", pointList.size(), getTaskName(), getJobDto().getOrganization());
        }
    }
}
