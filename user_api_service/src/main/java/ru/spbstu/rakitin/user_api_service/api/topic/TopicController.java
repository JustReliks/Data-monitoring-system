package ru.spbstu.rakitin.user_api_service.api.topic;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.rakitin.commonstarter.datamanagement.DataManagementManager;
import ru.spbstu.rakitin.dto.LightWeightTopicDto;
import ru.spbstu.rakitin.dto.kafka.KafkaMessageDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/topic")
@RequiredArgsConstructor
@Tag(name = "11. Операции с топиками")
public class TopicController {

    private final DataManagementManager dataManagementManager;

    @PostMapping("/")
    @Operation(description = "Создание топика")
    public Long createTopic(@RequestBody LightWeightTopicDto topicDto, Authentication authentication) {
        return dataManagementManager.createTopic(topicDto, authentication);
    }

    @GetMapping("/list")
    @Operation(description = "Получение списка топиков в проекте")
    public List<LightWeightTopicDto> list(@RequestParam(name = "projectId") long projectId, Authentication authentication) {
        return dataManagementManager.getAllTopicsForProjectId(projectId, authentication);
    }

    @GetMapping("/{topicId}")
    @Operation(description = "Получение топика по идентификатору")
    public LightWeightTopicDto findById(@PathVariable long topicId, Authentication authentication) {
        return dataManagementManager.findTopicByIdExternal(topicId, authentication);
    }

    @DeleteMapping("/{topicId}")
    @Operation(description = "Удаление топика")
    public void deleteTopic(@PathVariable long topicId, Authentication authentication) {
        dataManagementManager.deleteTopic(topicId, authentication);
    }

    @PostMapping("/{topicId}/message")
    @Operation(description = "Отправка сообщения в топик топика")
    public void sendMessageToTopic(@PathVariable long topicId, @RequestBody KafkaMessageDto kafkaMessageDto, Authentication authentication) {
        dataManagementManager.sendMessageToTopic(topicId, kafkaMessageDto, authentication);
    }

}
