package ru.spbstu.rakitin.requests.fulltext;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import ru.spbstu.rakitin.client.ApiName;
import ru.spbstu.rakitin.client.TaskTargetMdsRequest;
import ru.spbstu.rakitin.dto.TaskClientDto;
import ru.spbstu.rakitin.dto.fulltext.SolrQueryDto;

import java.util.List;
import java.util.Map;

public class FulltextQueryRequest extends TaskTargetMdsRequest<SolrQueryDto, List<Map<String, Object>>> {

    private final SolrQueryDto solrQueryDto;

    public FulltextQueryRequest(TaskClientDto taskClientDto, SolrQueryDto solrQueryDto) {
        super(taskClientDto);
        this.solrQueryDto = solrQueryDto;
    }

    @Override
    public ParameterizedTypeReference<List<Map<String, Object>>> getResponseClass() {
        return new ParameterizedTypeReference<>() {
        };
    }

    @Override
    public boolean hasResponse() {
        return true;
    }

    @Override
    public boolean hasBody() {
        return true;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.POST;
    }

    @Override
    public String buildPath() {
        return "/{configId}";
    }

    @Override
    public Map<String, Object> getUriVariables() {
        Map<String, Object> uriVariables = super.getUriVariables();
        uriVariables.put("configId", getTask().getTaskId());
        return uriVariables;
    }

    @Override
    public SolrQueryDto getBody() {
        return solrQueryDto;
    }

    @Override
    public ApiName apiName() {
        return ApiName.FULLTEXT_QUERY;
    }
}
