package ru.spbstu.rakitin.commonstarter.datamanagement;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.commonentites.model.Topic;
import ru.spbstu.rakitin.commonstarter.discovery.InnerServiceRequestFactory;
import ru.spbstu.rakitin.dto.ServiceName;
import ru.spbstu.rakitin.dto.JobNameDto;
import ru.spbstu.rakitin.dto.TopicDto;
import ru.spbstu.rakitin.dto.archive.ArchiveJobDto;
import ru.spbstu.rakitin.dto.fulltext.FulltextJobDto;
import ru.spbstu.rakitin.dto.monitoring.MonitoringJobDto;

import static ru.spbstu.rakitin.dto.ParametrizedTypes.TOPIC_TYPE;
import static ru.spbstu.rakitin.dto.ParametrizedTypes.VOID_TYPE;

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


}
