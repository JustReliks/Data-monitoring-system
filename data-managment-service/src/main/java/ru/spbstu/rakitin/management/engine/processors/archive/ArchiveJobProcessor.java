package ru.spbstu.rakitin.management.engine.processors.archive;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import ru.spbstu.rakitin.dto.archive.ArchiveJobDto;
import ru.spbstu.rakitin.dto.MapJson;
import ru.spbstu.rakitin.management.engine.AbstractJsonQueueProcessor;
import ru.spbstu.rakitin.management.engine.hdfs.HdfsConfigurationProperties;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Getter
public class ArchiveJobProcessor extends AbstractJsonQueueProcessor<ArchiveJobDto> {

    private final FileSystem fileSystem;
    private final String taskName;
    private final ArchiveJobDto archiveJobDto;
    private final HdfsConfigurationProperties hdfsConfigurationProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

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
                String dirName = getDirName();
                if (fileSystem.exists(new Path(dirName + filename))) {
                    if (archiveJobDto.isAccessOverwriting()) {
                        log.info("Overwriting archive file {} at {}", filename, dirName);
                    } else {
                        log.info("Archive file {} at {} already exists. Overwriting is disabled. Skip it.", filename, dirName);
                        return;
                    }
                }

                try (FSDataOutputStream outputStream = fileSystem.create(new Path(dirName, filename));
                     BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                ) {
                    bufferedWriter.write(objectMapper.writeValueAsString(mapJson));
                }


                log.info("Archive file {} successfully saved to path {}.", filename, dirName);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void close() {
        super.close();
        try {
            fileSystem.close();
        } catch (IOException e) {
            log.error("Unable to close hdfs file system", e);
            throw new RuntimeException(e);
        }
    }

    private String getDirName() {
        String folderName = archiveJobDto.getJobFolderName();
        String basePath = hdfsConfigurationProperties.getBasePath();

        return basePath + "/" + folderName + "/";
    }

    private String getFilename(MapJson mapJson) {
        String filename = mapJson.get(archiveJobDto.getSchema().getFilenameFieldName()).toString();
        if (FilenameUtils.getExtension(filename).isEmpty()) {
            filename += ".json";
        }
        return filename;
    }
}
