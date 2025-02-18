package ru.spbstu.rakitin.management.utils;

import lombok.experimental.UtilityClass;
import ru.spbstu.rakitin.commonentites.model.Topic;

@UtilityClass
public class KafkaTopicUtils {

    public String createKafkaTopicName(Topic topic) {
        return topic.getProject().getProjectName() + "." + topic.getName();
    }

}
