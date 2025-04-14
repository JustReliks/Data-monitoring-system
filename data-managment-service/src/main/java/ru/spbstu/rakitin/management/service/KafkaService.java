package ru.spbstu.rakitin.management.service;

import ru.spbstu.rakitin.commonentites.model.Topic;
import ru.spbstu.rakitin.management.exception.KafkaTopicCreationException;
import ru.spbstu.rakitin.management.exception.TopicAlreadyCreatedException;
import ru.spbstu.rakitin.management.exception.TopicNotFoundException;
import ru.spbstu.rakitin.management.exception.TopicQuotaLimitException;

public interface KafkaService {

    Topic createTopic(Topic topic) throws KafkaTopicCreationException, TopicAlreadyCreatedException, TopicQuotaLimitException;
    Topic findTopicById(long id) throws TopicNotFoundException;
}
