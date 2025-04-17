package ru.spbstu.rakitin.requests.monitoring;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import ru.spbstu.rakitin.client.ApiName;
import ru.spbstu.rakitin.client.TaskTargetMdsRequest;
import ru.spbstu.rakitin.dto.TaskClientDto;
import ru.spbstu.rakitin.dto.monitoring.ApiKeyDto;
import ru.spbstu.rakitin.dto.monitoring.CreateReadApiKeyDto;

import java.util.List;

public class CreateApiKey extends TaskTargetMdsRequest<CreateReadApiKeyDto, ApiKeyDto> {

    private final String description;

    public CreateApiKey(TaskClientDto taskClientDto, String description) {
        super(taskClientDto);
        this.description = description;
    }

    @Override
    public ParameterizedTypeReference<ApiKeyDto> getResponseClass() {
        return new ParameterizedTypeReference<ApiKeyDto>() {
        };
    }

    @Override
    public CreateReadApiKeyDto getBody() {
        CreateReadApiKeyDto body = new CreateReadApiKeyDto();
        body.setDescription(description);
        body.setProjectId(getTaskClientDto().getProjectId());
        body.setTasks(List.of(getTask().getTaskId()));

        return body;
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
        return "/create";
    }

    @Override
    public ApiName apiName() {
        return ApiName.MONITORING_API_KEY;
    }
}
