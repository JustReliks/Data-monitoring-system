package ru.spbstu.rakitin.management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.commonentites.model.Topic;
import ru.spbstu.rakitin.management.exception.TopicNotFoundException;
import ru.spbstu.rakitin.management.repository.TopicRepository;
import ru.spbstu.rakitin.management.service.TopicService;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;

    @Override
    public Topic findTopicById(long id) throws TopicNotFoundException {
        return topicRepository.findById(id).orElseThrow(() -> new TopicNotFoundException(String.format("Topic with id %s not found!", id)));
    }
}
