package ru.spbstu.rakitin.fulltext_service.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.Stack;

@Component
@Slf4j
public class SequentialEngine {

    public void performSequential(Queue<SequentialTask> sequentialTasks) throws Exception {
        Stack<SequentialTask> finishedTasks = new Stack<>();
        while (!sequentialTasks.isEmpty()) {
            SequentialTask nextTask = sequentialTasks.poll();
            try {
                nextTask.perform();
            } catch (Throwable throwable) {
                while (!finishedTasks.empty()) {
                    log.error("Unable to perform task. Rollback");
                    SequentialTask prevTask = finishedTasks.pop();
                    prevTask.rollback();
                }
                throw throwable;
            }
            finishedTasks.push(nextTask);
        }
    }

}
