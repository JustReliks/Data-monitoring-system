package ru.spbstu.rakitin.archive_service.engine;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.archive_service.model.ArchiveTaskConfig;
import ru.spbstu.rakitin.commonstarter.sequence.engine.SequentialEngine;
import ru.spbstu.rakitin.commonstarter.sequence.engine.SequentialTask;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

@Service
public class HdfsManager {

    public static final String PROJECT_FOLDER_CREATED = "PROJECT_FOLDER_CREATED";
    private final FileSystem fileSystem;
    private final SequentialEngine sequentialEngine;

    public HdfsManager(FileSystem fileSystem, SequentialEngine sequentialEngine) {
        this.fileSystem = fileSystem;
        this.sequentialEngine = sequentialEngine;
    }

    public void initiateTask(ArchiveTaskConfig config) throws Exception {
        Queue<SequentialTask> tasks = new LinkedList<>();
        String taskFolder = String.format("/archive/%s/%s", config.getProject().getProjectName(), config.getName());
        String projectFolder = String.format("/archive/%s", config.getProject().getProjectName());

        //Step 1: create project folder if not exists
        tasks.add(new SequentialTask() {
            @Override
            public void perform(Map<String, String> context) throws Exception {
                if (!fileSystem.exists(new Path(projectFolder))) {
                    fileSystem.mkdirs(new Path(projectFolder));
                    context.put(PROJECT_FOLDER_CREATED, Boolean.TRUE.toString());
                } else {
                    context.put(PROJECT_FOLDER_CREATED, Boolean.FALSE.toString());
                }
            }

            @Override
            public void rollback(Map<String, String> context) throws Exception {
                if (Boolean.getBoolean(context.get(PROJECT_FOLDER_CREATED))) {
                    fileSystem.delete(new Path(projectFolder), true);
                }
            }
        });
        sequentialEngine.performSequential(tasks);

        //Step 2: create task folder
        tasks.add(new SequentialTask() {
            @Override
            public void perform(Map<String, String> context) throws Exception {
                fileSystem.mkdirs(new Path(taskFolder));
            }

            @Override
            public void rollback(Map<String, String> context) throws Exception {
                fileSystem.delete(new Path(taskFolder), true);
            }
        });

        synchronized (sequentialEngine) {
            sequentialEngine.performSequential(tasks);
        }
    }


}
