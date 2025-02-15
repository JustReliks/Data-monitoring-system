package ru.spbstu.rakitin.commonstarter.datamanagement;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.commonentites.model.Topic;
import ru.spbstu.rakitin.commonstarter.discovery.InnerServiceRequestFactory;
import ru.spbstu.rakitin.commonstarter.discovery.ServiceName;
import ru.spbstu.rakitin.commonstarter.dto.fulltext.FulltextJobDto;
import ru.spbstu.rakitin.commonstarter.dto.fulltext.FulltextTaskConfigDto;

@Service
@RequiredArgsConstructor
public class DataManagementManager {

    private static final String FIND_TOPIC_BY_ID = "/api/internal/v1/topic/%s";
    private static final String START_FULLTEXT_JOB = "/api/v1/job/fulltext/start";

    private final InnerServiceRequestFactory innerServiceRequestFactory;

    public Topic findTopicById(long id, Authentication authentication) {
        return innerServiceRequestFactory.doGet(ServiceName.DATA_MANAGEMENT, authentication, String.format(FIND_TOPIC_BY_ID, id), Topic.class);
    }

    public void startFulltextJob(FulltextJobDto fulltextJobDto, Authentication authentication) {
        innerServiceRequestFactory.doPost(ServiceName.DATA_MANAGEMENT, authentication, START_FULLTEXT_JOB, fulltextJobDto, Void.TYPE);
    }

}
