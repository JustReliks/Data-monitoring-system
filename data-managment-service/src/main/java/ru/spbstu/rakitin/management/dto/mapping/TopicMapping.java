package ru.spbstu.rakitin.management.dto.mapping;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.spbstu.rakitin.commonentites.model.Topic;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.management.dto.TopicDto;

@Component
@RequiredArgsConstructor
public class TopicMapping {

    private final AdminManager adminManager;

    public Topic topicDtoToTopic(TopicDto topicDto) {
        return Topic.builder()
                .id(topicDto.getId())
                .name(topicDto.getName())
                .replicationFactor(topicDto.getReplicationFactor())
                .partitions(topicDto.getPartitions())
                .project(adminManager.findProjectById(topicDto.getProjectId())).build();
    }

}
