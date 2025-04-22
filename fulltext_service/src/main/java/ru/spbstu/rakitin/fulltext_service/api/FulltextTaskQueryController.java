package ru.spbstu.rakitin.fulltext_service.api;

import lombok.RequiredArgsConstructor;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.rakitin.commonstarter.admin.aspect.LogController;
import ru.spbstu.rakitin.dto.fulltext.SolrQueryDto;
import ru.spbstu.rakitin.dto.fulltext.SolrQueryResponseDto;
import ru.spbstu.rakitin.fulltext_service.exception.FulltextConfigNotFoundException;
import ru.spbstu.rakitin.fulltext_service.exception.FulltextTaskInstanceNotFoundException;
import ru.spbstu.rakitin.fulltext_service.exception.FulltextTaskInstanceNotRunningException;
import ru.spbstu.rakitin.fulltext_service.service.FulltextTaskService;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/fulltext/query")
public class FulltextTaskQueryController {

    private final FulltextTaskService fulltextTaskService;

    @PostMapping("/{taskId}")
    @LogController
    public SolrQueryResponseDto query(@RequestBody SolrQueryDto solrQuery, @PathVariable long taskId, Authentication authentication) throws FulltextConfigNotFoundException, FulltextTaskInstanceNotFoundException, SolrServerException, IOException, FulltextTaskInstanceNotRunningException {
        return fulltextTaskService.query(solrQuery, taskId, authentication);
    }

}
