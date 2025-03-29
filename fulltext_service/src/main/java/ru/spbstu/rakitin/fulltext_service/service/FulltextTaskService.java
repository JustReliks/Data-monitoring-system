package ru.spbstu.rakitin.fulltext_service.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.security.core.Authentication;
import ru.spbstu.rakitin.commonstarter.utils.MapJson;
import ru.spbstu.rakitin.commonstarter.dto.fulltext.SolrQueryDto;
import ru.spbstu.rakitin.fulltext_service.exception.FulltextConfigNotFoundException;
import ru.spbstu.rakitin.fulltext_service.exception.FulltextTaskInstanceNotFoundException;
import ru.spbstu.rakitin.fulltext_service.exception.FulltextTaskInstanceNotRunningException;

import java.io.IOException;
import java.util.List;

public interface FulltextTaskService {

    List<MapJson> query(SolrQueryDto query, long taskId, Authentication authentication) throws FulltextConfigNotFoundException, FulltextTaskInstanceNotFoundException, FulltextTaskInstanceNotRunningException, SolrServerException, IOException;

}
