package ru.spbstu.rakitin.archive_service.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import org.apache.hadoop.fs.*;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.archive_service.configuration.HdfsProperties;
import ru.spbstu.rakitin.dto.archive.FileDto;
import ru.spbstu.rakitin.dto.archive.FileInformationDto;
import ru.spbstu.rakitin.archive_service.exception.FileNotFoundInArchiveException;
import ru.spbstu.rakitin.archive_service.model.ArchiveTaskConfig;
import ru.spbstu.rakitin.commonstarter.sequence.engine.SequentialEngine;
import ru.spbstu.rakitin.commonstarter.sequence.engine.SequentialTask;
import ru.spbstu.rakitin.dto.MapJson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class HdfsManager {

    public static final String PROJECT_FOLDER_CREATED = "PROJECT_FOLDER_CREATED";

    private final ObjectMapper mapper = new ObjectMapper();

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
            public void perform(Map<String, Object> context) throws Exception {
                if (!fileSystem.exists(new Path(projectFolder))) {
                    fileSystem.mkdirs(new Path(projectFolder));
                    context.put(PROJECT_FOLDER_CREATED, Boolean.TRUE.toString());
                } else {
                    context.put(PROJECT_FOLDER_CREATED, Boolean.FALSE.toString());
                }
            }

            @Override
            public void rollback(Map<String, Object> context) throws Exception {
                if (Boolean.getBoolean(context.get(PROJECT_FOLDER_CREATED).toString())) {
                    fileSystem.delete(new Path(projectFolder), true);
                }
            }
        });
        sequentialEngine.performSequential(tasks);

        //Step 2: create task folder
        tasks.add(new SequentialTask() {
            @Override
            public void perform(Map<String, Object> context) throws Exception {
                fileSystem.mkdirs(new Path(taskFolder));
            }

            @Override
            public void rollback(Map<String, Object> context) throws Exception {
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
                    .filename(next.getPath().getName().replace(".json", ""))
                    .size(next.getLen()).build());
        }
        return result;
    }

    public FileDto getFile(String path) throws IOException, FileNotFoundInArchiveException {
        Path hdfsPath = new Path(hdfsProperties.getBasePath() + "/" + path);
        if (!fileSystem.exists(hdfsPath)) {
            throw new FileNotFoundInArchiveException("File not found: " + path);
        }
        RemoteIterator<LocatedFileStatus> locatedFileStatusRemoteIterator = fileSystem.listFiles(hdfsPath, false);
        LocatedFileStatus locatedFileStatus = locatedFileStatusRemoteIterator.next();
        String name = locatedFileStatus.getPath().getName();
        try (FSDataInputStream in = fileSystem.open(hdfsPath)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            MapJson mapJson = mapper.readValue(reader, MapJson.class);
            return FileDto.builder()
                    .fileData(mapJson)
                    .filename(name.replace(".json", ""))
                    .size(locatedFileStatus.getLen()).build();
        }

    }


    public void removeArchiveInstance(@NotNull ArchiveTaskConfig config) throws IOException {
        String taskFolder = String.format("/%s/%s/%s", hdfsProperties.getBasePath(), config.getProject().getProjectName(), config.getName());
        if (fileSystem.exists(new Path(taskFolder))) {
            fileSystem.delete(new Path(taskFolder), true);
        }
    }
}
