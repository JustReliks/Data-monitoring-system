package ru.spbstu.rakitin.management.dto.mapping;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.spbstu.rakitin.commonentites.model.Topic;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.dto.LightWeightTopicDto;

@Component
@RequiredArgsConstructor
public class TopicMapping {

    private final AdminManager adminManager;

    public Topic topicDtoToTopic(LightWeightTopicDto topicDto) {
        return Topic.builder()
                .id(topicDto.getId())
                .name(topicDto.getName())
                .replicationFactor(topicDto.getReplicationFactor())
                .partitions(topicDto.getPartitions())
                .project(adminManager.findProjectById(topicDto.getProjectId())).build();
    }

    public LightWeightTopicDto topicToTopicDto(Topic topic) {
        return LightWeightTopicDto.builder()
                .id(topic.getId())
                .name(topic.getName())
                .partitions(topic.getPartitions())
                .replicationFactor(topic.getReplicationFactor())
                .nameInKafka(topic.getNameInKafka())
                .projectId(topic.getProject().getId()).build();
    }

}
