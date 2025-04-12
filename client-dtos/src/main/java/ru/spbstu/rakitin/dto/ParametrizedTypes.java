package ru.spbstu.rakitin.dto;

import org.springframework.core.ParameterizedTypeReference;
import ru.spbstu.rakitin.dto.archive.ArchiveTaskResponse;
import ru.spbstu.rakitin.dto.archive.FileDto;
import ru.spbstu.rakitin.dto.archive.FileInformationDto;
import ru.spbstu.rakitin.dto.fulltext.FulltextTaskResponse;
import ru.spbstu.rakitin.dto.monitoring.ApiKeyDto;
import ru.spbstu.rakitin.dto.monitoring.MonitoringTaskResponse;

import java.util.List;

public class ParametrizedTypes {

    public static final ParameterizedTypeReference<List<FulltextTaskResponse>> LIST_FULLTEXT_TASK_RESPONSE_TYPE_REFERENCE = new ParameterizedTypeReference<>() {
    };
    public static final ParameterizedTypeReference<Long> LONG_TYPE = new ParameterizedTypeReference<>() {
    };
    public static final ParameterizedTypeReference<Object> VOID_TYPE = ParameterizedTypeReference.forType(Void.TYPE);
    public static final ParameterizedTypeReference<String> STRING_TYPE = new ParameterizedTypeReference<>() {
    };
    public static final ParameterizedTypeReference<TopicDto> TOPIC_TYPE = new ParameterizedTypeReference<>() {
    };
    public static final ParameterizedTypeReference<List<ArchiveTaskResponse>> LIST_ARCHIVE_TASK_RESPONSE_TYPE_REFERENCE = new ParameterizedTypeReference<>() {
    };
    public static final ParameterizedTypeReference<List<MonitoringTaskResponse>> LIST_MONITORING_TASK_RESPONSE_TYPE_REFERENCE = new ParameterizedTypeReference<>() {
    };
    public static final ParameterizedTypeReference<ApiKeyDto> API_KEY_REFERENCE = new ParameterizedTypeReference<>() {
    };

    public static final ParameterizedTypeReference<List<MapJson>> MAP_JSON_LIST = new ParameterizedTypeReference<>() {
    };

    public static final ParameterizedTypeReference<List<FileInformationDto>> FILE_INFORMATION_LIST = new ParameterizedTypeReference<>() {
    };
    public static final ParameterizedTypeReference<FileDto> FILE = new ParameterizedTypeReference<>() {
    };
    public static final ParameterizedTypeReference<Double> DOUBLE = new ParameterizedTypeReference<>() {
    };

}