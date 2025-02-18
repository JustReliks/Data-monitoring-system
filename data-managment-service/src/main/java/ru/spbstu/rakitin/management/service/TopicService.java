package ru.spbstu.rakitin.management.service;

import ru.spbstu.rakitin.commonentites.model.Topic;
import ru.spbstu.rakitin.management.exception.TopicNotFoundException;

public interface TopicService {

    Topic findTopicById(long id) throws TopicNotFoundException;

}
