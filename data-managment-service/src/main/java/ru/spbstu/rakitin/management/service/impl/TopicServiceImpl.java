package ru.spbstu.rakitin.management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.commonentites.model.Topic;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.dto.PermissionTypeEnum;
import ru.spbstu.rakitin.management.exception.KafkaTopicCreationException;
import ru.spbstu.rakitin.management.exception.TopicAlreadyCreatedException;
import ru.spbstu.rakitin.management.exception.TopicNotFoundException;
import ru.spbstu.rakitin.management.exception.TopicQuotaLimitException;
import ru.spbstu.rakitin.management.repository.TopicRepository;
import ru.spbstu.rakitin.management.service.KafkaService;
import ru.spbstu.rakitin.management.service.TopicService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final AdminManager adminManager;
    private final KafkaService kafkaService;


    @Override
    public Topic findTopicById(long id, Authentication authentication) throws TopicNotFoundException {
        Topic topic = topicRepository.findById(id).orElseThrow(() -> new TopicNotFoundException(String.format("Topic with id %s not found!", id)));
        adminManager.checkAccessThrowable(authentication, topic.getProject().getId(), PermissionTypeEnum.TOPIC_VIEW);
        return topic;
    }

    @Override
    public List<Topic> getAllTopicsForProjectId(long projectId, Authentication authentication) {
        adminManager.checkAccessThrowable(authentication, projectId, PermissionTypeEnum.TOPIC_VIEW);
        return topicRepository.findAllByProject_Id(projectId);
    }

    @Override
    public long createTopic(Topic topic, Authentication authentication) throws KafkaTopicCreationException, TopicAlreadyCreatedException, TopicQuotaLimitException {
        topic.setId(null);
        adminManager.checkAccessThrowable(authentication, topic.getProject().getId(), PermissionTypeEnum.TOPIC_CREATE);
        return kafkaService.createTopic(topic).getId();
    }

    @Override
    public void deleteTopic(long topicId, Authentication authentication) throws TopicNotFoundException {
        Topic topic = findTopicById(topicId, authentication);
        adminManager.checkAccessThrowable(authentication, topic.getProject().getId(), PermissionTypeEnum.TOPIC_CREATE);
        kafkaService.deleteTopic(topic);
        topicRepository.delete(topic);
    }

}
