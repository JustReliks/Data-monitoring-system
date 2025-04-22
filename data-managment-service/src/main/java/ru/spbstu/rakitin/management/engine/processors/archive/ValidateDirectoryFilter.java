package ru.spbstu.rakitin.management.engine.processors.archive;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.streams.kstream.Predicate;
import ru.spbstu.rakitin.dto.MapJson;
import ru.spbstu.rakitin.dto.archive.ArchiveJobDto;

import java.util.Arrays;

@RequiredArgsConstructor
@Slf4j
public class ValidateDirectoryFilter implements Predicate<String, MapJson> {

    private static final Character[] ILLEGAL_CHARACTERS = {'\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};

    private final ArchiveJobDto archiveJobDto;

    @Override
    public boolean test(String key, MapJson value) {
        String directoryFieldName = archiveJobDto.getSchema().getDirectoryFieldName();
        if (directoryFieldName == null) {
            return true;
        }
        String dir = value.get(directoryFieldName).toString();

        if (!dir.startsWith("/")) {
            log.warn("Directory field {} is not a valid directory", directoryFieldName);
            return false;
        }

        if (StringUtils.countMatches(dir, "/") > 1) {
            log.warn("Directory field {} contains more than one directory", directoryFieldName);
            return false;
        }

        if (Arrays.stream(ILLEGAL_CHARACTERS).anyMatch(character -> dir.contains(character.toString()))) {
            log.warn("The directory {} contains illegal characters", dir);
            return false;
        }


        return true;

    }
}
