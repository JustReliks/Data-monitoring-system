package ru.spbstu.rakitin.management.service;

import org.springframework.security.core.Authentication;
import ru.spbstu.rakitin.commonentites.model.Topic;
import ru.spbstu.rakitin.management.exception.KafkaTopicCreationException;
import ru.spbstu.rakitin.management.exception.TopicAlreadyCreatedException;
import ru.spbstu.rakitin.management.exception.TopicNotFoundException;
import ru.spbstu.rakitin.management.exception.TopicQuotaLimitException;

import java.util.List;

public interface TopicService {

    Topic findTopicById(long id, Authentication authentication) throws TopicNotFoundException;

    List<Topic> getAllTopicsForProjectId(long projectId, Authentication authentication);

    long createTopic(Topic topic, Authentication authentication) throws KafkaTopicCreationException, TopicAlreadyCreatedException, TopicQuotaLimitException;

    void deleteTopic(long topicId, Authentication authentication) throws TopicNotFoundException;
}
