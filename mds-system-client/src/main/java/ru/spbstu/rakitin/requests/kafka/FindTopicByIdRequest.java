package ru.spbstu.rakitin.requests.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import ru.spbstu.rakitin.client.ApiName;
import ru.spbstu.rakitin.client.MdsRequest;
import ru.spbstu.rakitin.dto.LightWeightTopicDto;

import java.util.Map;

@RequiredArgsConstructor
public class FindTopicByIdRequest extends MdsRequest<Void, LightWeightTopicDto> {

    private final Long topicId;

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
        return "/{topicId}";
    }

    @Override
    public Map<String, Object> getUriVariables() {
        Map<String, Object> uriVariables = super.getUriVariables();
        uriVariables.put("topicId", topicId);
        return uriVariables;
    }

    @Override
    public ApiName apiName() {
        return ApiName.TOPIC;
    }
}
