package ru.spbstu.rakitin.management.engine.processors.archive;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import ru.spbstu.rakitin.commonstarter.dto.archive.ArchiveJobDto;
import ru.spbstu.rakitin.commonstarter.utils.MapJson;
import ru.spbstu.rakitin.management.engine.hdfs.HdfsConfigurationProperties;
import ru.spbstu.rakitin.management.engine.processors.AbstractQueueProcessor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Getter
public class ArchiveJobProcessor extends AbstractQueueProcessor<ArchiveJobDto> {

    private final FileSystem fileSystem;
    private final String taskName;
    private final ArchiveJobDto archiveJobDto;
    private final HdfsConfigurationProperties hdfsConfigurationProperties;

    private final LinkedBlockingQueue<MapJson> queue = new LinkedBlockingQueue<>(100);


    public ArchiveJobProcessor(FileSystem fileSystem, String taskName, ArchiveJobDto archiveJobDto, HdfsConfigurationProperties hdfsConfigurationProperties) {
        super(archiveJobDto, taskName);
        this.fileSystem = fileSystem;
        this.taskName = taskName;
        this.archiveJobDto = archiveJobDto;
        this.hdfsConfigurationProperties = hdfsConfigurationProperties;
    }


    @Override
    protected void processQueue(LinkedBlockingQueue<MapJson> queue) throws Exception {
        Collection<MapJson> collection = new ArrayList<>();
        queue.drainTo(collection);


        collection.forEach(mapJson -> {
            try {
                String filename = getFilename(mapJson);
                Path dirName = getDirName(mapJson);
                if (fileSystem.exists(dirName)) {
                    if (archiveJobDto.isAccessOverwriting()) {
                        log.info("Overwriting archive file at {}", dirName);
                    } else {
                        log.info("Archive file at {} already exists. Overwriting is disabled. Skip it.", dirName);
                        return;
                    }
                }

                File file = Files.createTempFile(archiveJobDto.getTaskName() + "_" + filename + "_" + RandomStringUtils.random(5), ".tmp").toFile();
                try (PrintWriter writer = new PrintWriter(file)) {
                    writer.write(mapJson.toString());
                    fileSystem.copyFromLocalFile(true, archiveJobDto.isAccessOverwriting(),
                            new Path(file.toURI()),
                            dirName);

                    log.info("Archive file {} successfully saved to path {}.", filename, dirName);
                } finally {
                    file.delete();
                }

            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });
    }

    private Path getDirName(MapJson mapJson) {
        String folderName = archiveJobDto.getJobFolderName();
        String filename = getFilename(mapJson);
        String basePath = hdfsConfigurationProperties.getBasePath();

        return new Path(basePath + "/" + folderName + "/" + filename);
    }

    private String getFilename(MapJson mapJson) {
        return mapJson.get(archiveJobDto.getSchema().getFilenameFieldName()).toString();
    }
}
