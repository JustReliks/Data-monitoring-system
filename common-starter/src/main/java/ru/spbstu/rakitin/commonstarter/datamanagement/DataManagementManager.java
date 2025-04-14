package ru.spbstu.rakitin.commonstarter.datamanagement;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.commonstarter.discovery.InnerServiceRequestFactory;
import ru.spbstu.rakitin.dto.*;
import ru.spbstu.rakitin.dto.archive.ArchiveJobDto;
import ru.spbstu.rakitin.dto.fulltext.FulltextJobDto;
import ru.spbstu.rakitin.dto.monitoring.MonitoringJobDto;

import java.util.List;

import static ru.spbstu.rakitin.dto.ParametrizedTypes.*;

@Service
@RequiredArgsConstructor
public class DataManagementManager {

    private static final String FIND_TOPIC_BY_ID = "/api/internal/v1/topic/%s";
    private static final String START_FULLTEXT_JOB = "/api/v1/job/fulltext/start";
    private static final String STOP_FULLTEXT_JOB = "/api/v1/job/fulltext/stop";
    private static final String STOP_ARCHIVE_JOB = "/api/v1/job/archive/stop";
    private static final String START_ARCHIVE_JOB = "/api/v1/job/archive/start";
    private static final String STOP_MONITORING_JOB = "/api/v1/job/monitoring/stop";
    private static final String START_MONITORING_JOB = "/api/v1/job/monitoring/start";
    private static final String FIND_TOPIC_BY_ID_EXTERNAL = "/api/v1/topic/%s";
    private static final String CREATE_TOPIC = "/api/v1/topic/";
    private static final String LIST_TOPIC_BY_PROJECT_ID = "/api/v1/topic/list?projectId=%s";
    private static final String PUBLIC_KAFKA_PROPERTIES = "/api/v1/kafka/properties";

    private final InnerServiceRequestFactory innerServiceRequestFactory;

    public TopicDto findTopicById(long id, Authentication authentication) {
        return innerServiceRequestFactory.doGet(ServiceName.DATA_MANAGEMENT, authentication, String.format(FIND_TOPIC_BY_ID, id), TOPIC_TYPE);
    }

    public void startFulltextJob(FulltextJobDto fulltextJobDto, Authentication authentication) {
        innerServiceRequestFactory.doPost(ServiceName.DATA_MANAGEMENT, authentication, START_FULLTEXT_JOB, fulltextJobDto, VOID_TYPE);
    }

    public void stopFulltextJob(JobNameDto jobName, Authentication authentication) {
        innerServiceRequestFactory.doPost(ServiceName.DATA_MANAGEMENT, authentication, STOP_FULLTEXT_JOB, jobName, VOID_TYPE);
    }

    public void startArchiveJob(ArchiveJobDto archiveJobDto, Authentication authentication) {
        innerServiceRequestFactory.doPost(ServiceName.DATA_MANAGEMENT, authentication, START_ARCHIVE_JOB, archiveJobDto, VOID_TYPE);
    }

    public void stopArchiveJob(JobNameDto jobName, Authentication authentication) {
        innerServiceRequestFactory.doPost(ServiceName.DATA_MANAGEMENT, authentication, STOP_ARCHIVE_JOB, jobName, VOID_TYPE);
    }

    public void startMonitoringJob(MonitoringJobDto monitoringJobDto, Authentication authentication) {
        innerServiceRequestFactory.doPost(ServiceName.DATA_MANAGEMENT, authentication, START_MONITORING_JOB, monitoringJobDto, VOID_TYPE);
    }

    public void stopMonitoringJob(JobNameDto jobName, Authentication authentication) {
        innerServiceRequestFactory.doPost(ServiceName.DATA_MANAGEMENT, authentication, STOP_MONITORING_JOB, jobName, VOID_TYPE);
    }


    public Long createTopic(LightWeightTopicDto topicDto, Authentication authentication) {
        return innerServiceRequestFactory.doPost(ServiceName.DATA_MANAGEMENT, authentication, CREATE_TOPIC, topicDto, LONG_TYPE);
    }

    public List<LightWeightTopicDto> getAllTopicsForProjectId(long projectId, Authentication authentication) {
        return innerServiceRequestFactory.doGet(ServiceName.DATA_MANAGEMENT, authentication, String.format(LIST_TOPIC_BY_PROJECT_ID, projectId), LIST_LIGHT_WEIGHT_TOPIC);
    }

    public LightWeightTopicDto findTopicByIdExternal(long topicId, Authentication authentication) {
        return innerServiceRequestFactory.doGet(ServiceName.DATA_MANAGEMENT, authentication, String.format(FIND_TOPIC_BY_ID_EXTERNAL, topicId), LIGHT_WEIGHT_TOPIC);
    }

    public MapJson getPublicProperties(Authentication authentication) {
        return innerServiceRequestFactory.doGet(ServiceName.DATA_MANAGEMENT, authentication, PUBLIC_KAFKA_PROPERTIES, MAP_JSON);
    }
}
