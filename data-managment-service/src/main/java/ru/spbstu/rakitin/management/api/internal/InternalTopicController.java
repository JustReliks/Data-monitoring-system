package ru.spbstu.rakitin.management.api.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.rakitin.commonentites.model.Topic;
import ru.spbstu.rakitin.management.exception.TopicNotFoundException;
import ru.spbstu.rakitin.management.service.TopicService;

@RestController
@RequestMapping("/api/internal/v1/topic")
@RequiredArgsConstructor
public class InternalTopicController {

    private final TopicService topicService;

    @GetMapping("/{id}")
    public Topic findTopicById(@PathVariable("id") long id, Authentication authentication) throws TopicNotFoundException {
        return topicService.findTopicById(id, authentication);
    }


}
