package ru.spbstu.rakitin.dto;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.spbstu.rakitin.dto.archive.ArchiveTaskResponse;
import ru.spbstu.rakitin.dto.fulltext.FulltextTaskResponse;
import ru.spbstu.rakitin.dto.monitoring.MonitoringTaskResponse;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FulltextTaskResponse.class, name = "FulltextTaskResponse"),
        @JsonSubTypes.Type(value = ArchiveTaskResponse.class, name = "ArchiveTaskResponse"),
        @JsonSubTypes.Type(value = MonitoringTaskResponse.class, name = "MonitoringTaskResponse")
})
public interface TaskInformator {

    long getTopicId();

    long getTaskId();

    long getProjectId();

}
