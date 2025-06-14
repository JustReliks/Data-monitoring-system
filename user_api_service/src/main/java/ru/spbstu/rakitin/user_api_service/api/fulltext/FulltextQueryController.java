package ru.spbstu.rakitin.user_api_service.api.fulltext;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.rakitin.commonstarter.admin.aspect.LogController;
import ru.spbstu.rakitin.dto.fulltext.SolrQueryDto;
import ru.spbstu.rakitin.commonstarter.fulltext.FulltextServiceManager;
import ru.spbstu.rakitin.dto.MapJson;
import ru.spbstu.rakitin.dto.fulltext.SolrQueryResponseDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fulltext/query")
@RequiredArgsConstructor
@Tag(name = "6. Отправка запроса в полнотекстовые задачи")
public class FulltextQueryController {

    private final FulltextServiceManager fulltextServiceManager;

    @PostMapping("/{taskId}")
    @LogController
    @Operation(description = "Запрос в полнотекстовое хранилище")
    public SolrQueryResponseDto query(@RequestBody SolrQueryDto solrQuery, @PathVariable long taskId, Authentication authentication) {
        return fulltextServiceManager.query(solrQuery, taskId, authentication);


    }
}
