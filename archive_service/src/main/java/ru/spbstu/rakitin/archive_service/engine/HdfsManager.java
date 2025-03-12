package ru.spbstu.rakitin.archive_service.engine;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.archive_service.configuration.HdfsProperties;
import ru.spbstu.rakitin.archive_service.dto.FileInformationDto;
import ru.spbstu.rakitin.archive_service.model.ArchiveTaskConfig;
import ru.spbstu.rakitin.commonstarter.sequence.engine.SequentialEngine;
import ru.spbstu.rakitin.commonstarter.sequence.engine.SequentialTask;

import java.io.IOException;
import java.util.*;

@Service
public class HdfsManager {

    public static final String PROJECT_FOLDER_CREATED = "PROJECT_FOLDER_CREATED";
    private final FileSystem fileSystem;
    private final SequentialEngine sequentialEngine;
    private final HdfsProperties hdfsProperties;

    public HdfsManager(FileSystem fileSystem, SequentialEngine sequentialEngine, HdfsProperties hdfsProperties) {
        this.fileSystem = fileSystem;
        this.sequentialEngine = sequentialEngine;
        this.hdfsProperties = hdfsProperties;
    }

    public void initiateTask(ArchiveTaskConfig config) throws Exception {
        Queue<SequentialTask> tasks = new LinkedList<>();
        String taskFolder = String.format("/%s/%s/%s", hdfsProperties.getBasePath(), config.getProject().getProjectName(), config.getName());
        String projectFolder = String.format("/%s/%s", hdfsProperties.getBasePath(), config.getProject().getProjectName());

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

    public List<FileInformationDto> getAllFilesInDirectory(String path) throws IOException {
        RemoteIterator<LocatedFileStatus> locatedFileStatusRemoteIterator = fileSystem.listFiles(new Path(hdfsProperties.getBasePath() + "/" + path), true);
        List<FileInformationDto> result = new ArrayList<>();
        while (locatedFileStatusRemoteIterator.hasNext()) {
            LocatedFileStatus next = locatedFileStatusRemoteIterator.next();

            result.add(FileInformationDto.builder()
                    .filename(next.getPath().getName())
                    .size(next.getLen()).build());
        }
        return result;
    }


}
