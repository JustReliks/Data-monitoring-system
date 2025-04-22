package ru.spbstu.rakitin.management.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.commonentites.model.Topic;
import ru.spbstu.rakitin.management.exception.KafkaTopicCreationException;
import ru.spbstu.rakitin.management.exception.TopicAlreadyCreatedException;
import ru.spbstu.rakitin.management.exception.TopicNotFoundException;
import ru.spbstu.rakitin.management.exception.TopicQuotaLimitException;
import ru.spbstu.rakitin.management.repository.TopicRepository;
import ru.spbstu.rakitin.management.service.KafkaService;
import ru.spbstu.rakitin.management.utils.KafkaTopicUtils;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class KafkaServiceImpl implements KafkaService {

    private final TopicRepository topicRepository;

    private final Admin admin;

    @Override
    public Topic createTopic(Topic topic) throws KafkaTopicCreationException, TopicAlreadyCreatedException, TopicQuotaLimitException {
        String kafkaTopicName = KafkaTopicUtils.createKafkaTopicName(topic);
        Optional<Topic> findTopic = topicRepository.findByNameInKafka(kafkaTopicName);
        if (findTopic.isPresent()) {
            throw new TopicAlreadyCreatedException("Kafka with name " + topic.getName() + " already exists!");
        }
        int topicCount = topicRepository.countAllByProject_Id(topic.getProject().getId());
        if (topicCount >= topic.getProject().getTopicQuota()) {
            throw new TopicQuotaLimitException("Topic quota limit exceeded for project " + topic.getProject().getId());
        }
        topic.setNameInKafka(kafkaTopicName);
        NewTopic newTopic = new NewTopic(kafkaTopicName, topic.getPartitions(), (short) topic.getReplicationFactor());
        CreateTopicsResult result = admin.createTopics(Collections.singleton(newTopic));
        try {
            topic.setUuid(result.topicId(kafkaTopicName).get().toString());
        } catch (InterruptedException | ExecutionException e) {
            throw new KafkaTopicCreationException(String.format("Unable to create topic %s!", topic.getName()), e);
        }
        return topicRepository.save(topic);
    }

    @Override
    public Topic findTopicById(long id) throws TopicNotFoundException {
        return topicRepository.findById(id).orElseThrow(() -> new TopicNotFoundException(String.format("Topic with id %s not found!", id)));
    }

    @Override
    public void deleteTopic(Topic topic) {
        admin.deleteTopics(Collections.singleton(topic.getNameInKafka()));
    }
}
