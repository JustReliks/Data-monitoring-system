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
        int step = 0;
        Stack<SequentialTask> finishedTasks = new Stack<>();
        Map<String, Object> context = new HashMap<>();
        while (!sequentialTasks.isEmpty()) {
            SequentialTask nextTask = sequentialTasks.poll();
            try {
                step++;
                nextTask.perform(context);
                log.info("Finished sequential task on step {}", step);
            } catch (Throwable throwable) {
                log.error("Error while performing sequential task on step {}", step, throwable);
                while (!finishedTasks.empty()) {
                    step--;
                    log.warn("Rollback step {}", step);
                    SequentialTask prevTask = finishedTasks.pop();
                    prevTask.rollback(context);
                    log.warn("Step {} rollback successfully", step);
                }
                throw throwable;
            }
            finishedTasks.push(nextTask);
        }
    }

}
