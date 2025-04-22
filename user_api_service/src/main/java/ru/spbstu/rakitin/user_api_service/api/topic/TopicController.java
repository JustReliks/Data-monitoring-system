package ru.spbstu.rakitin.user_api_service.api.topic;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.rakitin.commonstarter.datamanagement.DataManagementManager;
import ru.spbstu.rakitin.dto.LightWeightTopicDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/topic")
@RequiredArgsConstructor
public class TopicController {

    private final DataManagementManager dataManagementManager;

    @PostMapping("/")
    public Long createTopic(@RequestBody LightWeightTopicDto topicDto, Authentication authentication) {
        return dataManagementManager.createTopic(topicDto, authentication);
    }

    @GetMapping("/list")
    public List<LightWeightTopicDto> list(@RequestParam(name = "projectId") long projectId, Authentication authentication) {
        return dataManagementManager.getAllTopicsForProjectId(projectId, authentication);
    }

    @GetMapping("/{topicId}")
    public LightWeightTopicDto findById(@PathVariable long topicId, Authentication authentication) {
        return dataManagementManager.findTopicByIdExternal(topicId, authentication);
    }

    @DeleteMapping("/{topicId}")
    public void deleteTopic(@PathVariable long topicId, Authentication authentication) {
        dataManagementManager.deleteTopic(topicId, authentication);
    }

}
