package ru.spbstu.rakitin.management.engine.processors.archive;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.kafka.streams.kstream.Predicate;
import ru.spbstu.rakitin.commonstarter.dto.archive.ArchiveJobDto;
import ru.spbstu.rakitin.commonstarter.utils.MapJson;

import java.util.Arrays;

@RequiredArgsConstructor
@Slf4j
public class ValidateFilenameFilter implements Predicate<String, MapJson> {

    private static final Character[] ILLEGAL_CHARACTERS = {'/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};

    private final ArchiveJobDto archiveJobDto;

    @Override
    public boolean test(String key, MapJson value) {
        String filenameFieldName = archiveJobDto.getSchema().getFilenameFieldName();
        String filename = value.get(filenameFieldName).toString();
        String extension = FilenameUtils.getExtension(filename);

        boolean correct = extension.isEmpty() || extension.equals("json");
        if (!correct) {
            log.warn("The filename {} have unsupported extension. Supported: no extension or json", filename);
            return false;
        }


        if (Arrays.stream(ILLEGAL_CHARACTERS).anyMatch(character -> filename.contains(character.toString()))) {
            log.warn("The filename {} contains illegal characters", filename);
            return false;
        }


        return true;
    }
}
