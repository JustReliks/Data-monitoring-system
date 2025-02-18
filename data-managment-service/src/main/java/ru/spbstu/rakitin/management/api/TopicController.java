package ru.spbstu.rakitin.management.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.rakitin.commonentites.model.PermissionTypeEnum;
import ru.spbstu.rakitin.commonstarter.admin.aspect.CheckPermission;
import ru.spbstu.rakitin.commonstarter.admin.aspect.LogController;
import ru.spbstu.rakitin.commonstarter.admin.aspect.ProjectIdContainer;
import ru.spbstu.rakitin.management.dto.TopicDto;
import ru.spbstu.rakitin.management.dto.mapping.TopicMapping;
import ru.spbstu.rakitin.management.exception.KafkaTopicCreationException;
import ru.spbstu.rakitin.management.exception.TopicAlreadyCreatedException;
import ru.spbstu.rakitin.management.exception.TopicQuotaLimitException;
import ru.spbstu.rakitin.management.service.KafkaService;

@RestController
@RequestMapping("/api/v1/topic")
@RequiredArgsConstructor
public class TopicController {

    private final KafkaService kafkaService;
    private final TopicMapping topicMapping;

    @PostMapping("/")
    @CheckPermission(permission = PermissionTypeEnum.TOPIC_CREATE, projectIdField = "topicDto", userIdField = "authentication")
    @LogController
    public void createTopic(@ProjectIdContainer(innerFieldName = "projectId") @RequestBody TopicDto topicDto, Authentication authentication) throws KafkaTopicCreationException, TopicAlreadyCreatedException, TopicQuotaLimitException {
        kafkaService.createTopic(topicMapping.topicDtoToTopic(topicDto));

    }

}
