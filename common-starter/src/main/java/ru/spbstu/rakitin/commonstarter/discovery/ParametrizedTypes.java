package ru.spbstu.rakitin.commonstarter.discovery;

import org.springframework.core.ParameterizedTypeReference;
import ru.spbstu.rakitin.commonentites.model.Topic;
import ru.spbstu.rakitin.commonstarter.dto.archive.ArchiveTaskResponse;
import ru.spbstu.rakitin.commonstarter.dto.fulltext.FulltextTaskResponse;
import ru.spbstu.rakitin.commonstarter.dto.monitoring.ApiKeyDto;
import ru.spbstu.rakitin.commonstarter.dto.monitoring.MonitoringTaskResponse;

import java.util.List;

public class ParametrizedTypes {

    public static final ParameterizedTypeReference<List<FulltextTaskResponse>> LIST_FULLTEXT_TASK_RESPONSE_TYPE_REFERENCE = new ParameterizedTypeReference<>() {
    };
    public static final ParameterizedTypeReference<Long> LONG_TYPE = new ParameterizedTypeReference<>() {
    };
    public static final ParameterizedTypeReference<Object> VOID_TYPE = ParameterizedTypeReference.forType(Void.TYPE);
    public static final ParameterizedTypeReference<String> STRING_TYPE = new ParameterizedTypeReference<>() {
    };
    public static final ParameterizedTypeReference<Topic> TOPIC_TYPE = new ParameterizedTypeReference<>() {
    };
    public static final ParameterizedTypeReference<List<ArchiveTaskResponse>> LIST_ARCHIVE_TASK_RESPONSE_TYPE_REFERENCE = new ParameterizedTypeReference<>() {
    };
    public static final ParameterizedTypeReference<List<MonitoringTaskResponse>> LIST_MONITORING_TASK_RESPONSE_TYPE_REFERENCE = new ParameterizedTypeReference<>() {
    };
    public static final ParameterizedTypeReference<ApiKeyDto> API_KEY_REFERENCE = new ParameterizedTypeReference<>() {
    };
}