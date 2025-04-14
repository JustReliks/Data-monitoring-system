package ru.spbstu.rakitin.requests.fulltext;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import ru.spbstu.rakitin.client.ApiName;
import ru.spbstu.rakitin.client.MdsRequest;
import ru.spbstu.rakitin.dto.ParametrizedTypes;
import ru.spbstu.rakitin.dto.fulltext.FulltextTaskResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FulltextTaskListRequest extends MdsRequest<Void, List<FulltextTaskResponse>> {

    private final long[] projectIds;

    public FulltextTaskListRequest(long... projects) {
        assert projects.length > 0;
        this.projectIds = projects;
    }

    @Override
    public MultiValueMap<String, String> getRequestParams() {
        return MultiValueMap.fromMultiValue(Map.of("projects", Arrays.stream(projectIds).mapToObj(String::valueOf).collect(Collectors.toList())));
    }

    @Override
    public ParameterizedTypeReference<List<FulltextTaskResponse>> getResponseClass() {
        return ParametrizedTypes.LIST_FULLTEXT_TASK_RESPONSE_TYPE_REFERENCE;
    }

    @Override
    public boolean hasResponse() {
        return true;
    }

    @Override
    public boolean hasBody() {
        return false;
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.GET;
    }

    @Override
    public String buildPath() {
        return "/list";
    }

    @Override
    public ApiName apiName() {
        return ApiName.FULLTEXT_CONFIG;
    }
}
