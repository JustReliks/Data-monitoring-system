package ru.spbstu.rakitin.management;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;

public class HadoopAccessTest {


    @Test
    public void accessTest() throws IOException, InterruptedException {
        try {
            System.setProperty("hadoop.home.dir", "C:/hadoop");

            Configuration conf = new Configuration();
            conf.set("dfs.client.use.datanode.hostname", "true");
            FileSystem fileSystem = FileSystem.get(URI.create("hdfs://localhost:9000"), conf, "archive_user");

            try (FSDataOutputStream outputStream = fileSystem.create(new Path("/test"))) {
                Files.copy(new File("D:/programming/MDS-system/docker-compose.yml").toPath(), outputStream);
            }
        } catch (Exception e) {

        }
    }

}
