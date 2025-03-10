package ru.spbstu.rakitin.commonstarter.sequence.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

@Component
@Slf4j
public class SequentialEngine {

    public void performSequential(Queue<SequentialTask> sequentialTasks) throws Exception {
        Stack<SequentialTask> finishedTasks = new Stack<>();
        Map<String, String> context = new HashMap<>();
        while (!sequentialTasks.isEmpty()) {
            SequentialTask nextTask = sequentialTasks.poll();
            try {
                nextTask.perform(context);
            } catch (Throwable throwable) {
                while (!finishedTasks.empty()) {
                    log.error("Unable to perform task. Rollback");
                    SequentialTask prevTask = finishedTasks.pop();
                    prevTask.rollback(context);
                }
                throw throwable;
            }
            finishedTasks.push(nextTask);
        }
    }

}
