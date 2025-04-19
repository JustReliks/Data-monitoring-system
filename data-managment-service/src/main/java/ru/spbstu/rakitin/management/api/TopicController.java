package ru.spbstu.rakitin.management.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.rakitin.dto.PermissionTypeEnum;
import ru.spbstu.rakitin.commonstarter.admin.aspect.CheckPermission;
import ru.spbstu.rakitin.commonstarter.admin.aspect.LogController;
import ru.spbstu.rakitin.commonstarter.admin.aspect.ProjectIdContainer;
import ru.spbstu.rakitin.dto.LightWeightTopicDto;
import ru.spbstu.rakitin.management.dto.mapping.TopicMapping;
import ru.spbstu.rakitin.management.exception.KafkaTopicCreationException;
import ru.spbstu.rakitin.management.exception.TopicAlreadyCreatedException;
import ru.spbstu.rakitin.management.exception.TopicNotFoundException;
import ru.spbstu.rakitin.management.exception.TopicQuotaLimitException;
import ru.spbstu.rakitin.management.service.TopicService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/topic")
@RequiredArgsConstructor
public class TopicController {

    private final TopicMapping topicMapping;
    private final TopicService topicService;

    @PostMapping("/")
    @CheckPermission(permission = PermissionTypeEnum.TOPIC_CREATE, projectIdField = "topicDto", userIdField = "authentication")
    @LogController
    public Long createTopic(@ProjectIdContainer(innerFieldName = "projectId") @RequestBody LightWeightTopicDto topicDto, Authentication authentication) throws KafkaTopicCreationException, TopicAlreadyCreatedException, TopicQuotaLimitException {
        return topicService.createTopic(topicMapping.topicDtoToTopic(topicDto), authentication);
    }

    @GetMapping("/list")
    public List<LightWeightTopicDto> list(@RequestParam(name = "projectId") long projectId, Authentication authentication) {
        return topicService.getAllTopicsForProjectId(projectId, authentication).stream().map(
                topicMapping::topicToTopicDto
        ).toList();
    }

    @GetMapping("/{topicId}")
    public LightWeightTopicDto findById(@PathVariable long topicId, Authentication authentication) throws TopicNotFoundException {
        return topicMapping.topicToTopicDto(topicService.findTopicById(topicId, authentication));
    }

}
