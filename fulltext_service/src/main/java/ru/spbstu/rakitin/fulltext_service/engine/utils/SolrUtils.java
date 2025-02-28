package ru.spbstu.rakitin.fulltext_service.engine.utils;

import lombok.experimental.UtilityClass;
import ru.spbstu.rakitin.fulltext_service.model.FulltextTaskConfig;

import java.util.Date;

@UtilityClass
public class SolrUtils {

    private static final String COLLECTION_NAME_PATTERN = "%s.%s.%s";
    private static final String ALIAS_NAME_PATTERN = "%s.%s_%s";


    public String buildCollectionName(FulltextTaskConfig config) {
        return String.format(COLLECTION_NAME_PATTERN, config.getProject().getProjectName(), config.getName(), new Date().getTime());
    }

    public String buildWriteCollectionName(String projectName, String taskName) {
        return String.format(ALIAS_NAME_PATTERN, projectName, taskName, "WRITE");
    }

    public String buildReadCollectionName(String projectName, String taskName) {
        return String.format(ALIAS_NAME_PATTERN, projectName, taskName, "READ");
    }


}
