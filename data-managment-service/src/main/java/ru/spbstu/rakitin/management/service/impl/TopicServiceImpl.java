package ru.spbstu.rakitin.management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.spbstu.rakitin.commonentites.model.Topic;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.commonstarter.discovery.AdminUserService;
import ru.spbstu.rakitin.dto.PermissionTypeEnum;
import ru.spbstu.rakitin.dto.TaskType;
import ru.spbstu.rakitin.dto.kafka.KafkaMessageDto;
import ru.spbstu.rakitin.management.dto.TaskDto;
import ru.spbstu.rakitin.management.exception.*;
import ru.spbstu.rakitin.management.repository.TopicRepository;
import ru.spbstu.rakitin.management.service.KafkaService;
import ru.spbstu.rakitin.management.service.TaskService;
import ru.spbstu.rakitin.management.service.TopicService;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final AdminManager adminManager;
    private final KafkaService kafkaService;
    private final TaskService taskService;

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
    @Transactional
    public void deleteTopic(long topicId, Authentication authentication) throws TopicNotFoundException, TopicDeleteForbidden {
        Topic topic = findTopicById(topicId, authentication);
        adminManager.checkAccessThrowable(authentication, topic.getProject().getId(), PermissionTypeEnum.TOPIC_CREATE);
        List<TaskDto> tasks = taskService.getTasksListForProjectsAndTaskTypes(List.of(topic.getProject().getId()), Arrays.stream(TaskType.values()).toList());
        if(tasks.stream().anyMatch(taskDto -> taskDto.getJobDto().getTopicId() == topicId)) {
            throw new TopicDeleteForbidden(String.format("Unable to delete topic with id %s because there are running tasks for this topic", topicId));
        }
        topicRepository.delete(topic);
        kafkaService.deleteTopic(topic);
    }

    @Override
    public void sendMessageToTopic(long topicId, KafkaMessageDto message, Authentication authentication) throws TopicNotFoundException {
        Topic topic = findTopicById(topicId, authentication);
        kafkaService.sendMessageToTopic(topic.getNameInKafka(), message.getMessage(), message.getKey());
    }

}
