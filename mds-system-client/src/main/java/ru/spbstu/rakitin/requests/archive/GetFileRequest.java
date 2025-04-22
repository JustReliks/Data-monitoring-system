package ru.spbstu.rakitin.requests.archive;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import ru.spbstu.rakitin.client.ApiName;
import ru.spbstu.rakitin.client.TaskTargetMdsRequest;
import ru.spbstu.rakitin.dto.TaskClientDto;
import ru.spbstu.rakitin.dto.archive.FileDto;

import java.util.Map;

public class GetFileRequest extends TaskTargetMdsRequest<Void, FileDto> {

    private final String directory;
    private final String fileName;

    public GetFileRequest(TaskClientDto taskClientDto, String directory, String fileName) {
        super(taskClientDto);
        this.directory = directory;
        this.fileName = fileName;
    }

    @Override
    public ParameterizedTypeReference<FileDto> getResponseClass() {
        return new ParameterizedTypeReference<FileDto>() {
        };
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
    public MultiValueMap<String, String> getRequestParams() {
        MultiValueMap<String, String> requestParams = super.getRequestParams();
        if (StringUtils.hasLength(directory)) {
            requestParams.add("directory", directory);
        }
        return requestParams;
    }

    @Override
    public Map<String, Object> getUriVariables() {
        Map<String, Object> uriVariables = super.getUriVariables();
        uriVariables.put("configId", getTask().getTaskId());
        uriVariables.put("filename", fileName);
        return uriVariables;
    }

    @Override
    public String buildPath() {
        return "/{configId}/file/{filename}";
    }

    @Override
    public ApiName apiName() {
        return ApiName.ARCHIVE_QUERY;
    }
}
