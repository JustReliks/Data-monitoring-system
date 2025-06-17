package ru.spbstu.rakitin.management.service;

import org.springframework.security.core.Authentication;
import ru.spbstu.rakitin.commonentites.model.Topic;
import ru.spbstu.rakitin.dto.kafka.KafkaMessageDto;
import ru.spbstu.rakitin.management.exception.*;

import java.util.List;

public interface TopicService {

    Topic findTopicById(long id, Authentication authentication) throws TopicNotFoundException;

    List<Topic> getAllTopicsForProjectId(long projectId, Authentication authentication);

    long createTopic(Topic topic, Authentication authentication) throws KafkaTopicCreationException, TopicAlreadyCreatedException, TopicQuotaLimitException;

    void deleteTopic(long topicId, Authentication authentication) throws TopicNotFoundException, TopicDeleteForbidden;

    void sendMessageToTopic(long topicId, KafkaMessageDto message, Authentication authentication) throws TopicNotFoundException;
}
