package ru.spbstu.rakitin.commonstarter.datamanagement;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.commonentites.model.Topic;
import ru.spbstu.rakitin.commonstarter.discovery.InnerServiceRequestFactory;
import ru.spbstu.rakitin.commonstarter.discovery.ServiceName;

@Service
@RequiredArgsConstructor
public class DataManagementManager {

    private static final String FIND_TOPIC_BY_ID = "/api/internal/v1/topic/%s";

    private final InnerServiceRequestFactory innerServiceRequestFactory;

    public Topic findTopicById(long id, Authentication authentication) {
        return innerServiceRequestFactory.doGet(ServiceName.DATA_MANAGEMENT, authentication, String.format(FIND_TOPIC_BY_ID, id), Topic.class);
    }

}
