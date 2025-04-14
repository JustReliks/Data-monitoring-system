package ru.spbstu.rakitin.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ApiName {

    AUTH("/api/v1/user"), ARCHIVE_INSTANCE("/api/v1/archive/instance"), ARCHIVE_CONFIG("/api/v1/archive/config"),
    ARCHIVE_QUERY("/api/v1/archive/query"), FULLTEXT_INSTANCE("/api/v1/fulltext/instance"), FULLTEXT_CONFIG("/api/v1/fulltext/config"),
    FULLTEXT_QUERY("/api/v1/fulltext/query"), MONITORING_INSTANCE("/api/v1/monitoring/instance"), MONITORING_CONFIG("/api/v1/monitoring/config"),
    MONITORING_API_KEY("/api/v1/monitoring/key"), TOPIC("/api/v1/topic"), KAFKA("/api/v1/kafka");

    private final String baseUrl;

}
