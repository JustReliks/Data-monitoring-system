package ru.spbstu.rakitin;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

public abstract class MdsRequest<T, R> {

    public String buildFullPath() {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(apiName().getBaseUrl());
        uriComponentsBuilder.path(buildPath());
        uriComponentsBuilder.uriVariables(getUriVariables())
                .queryParams(getRequestParams());
        return uriComponentsBuilder.build().toString();
    }

    public ParameterizedTypeReference<R> getResponseClass() {
        return null;
    }

    public MultiValueMap<String, String> getRequestParams() {
        return new LinkedMultiValueMap<>();
    }

    public Map<String, Object> getUriVariables() {
        return new HashMap<>();
    }

    public boolean needAuthentication() {
        return true;
    }

    public T getBody() {
        return null;
    }

    public abstract boolean hasResponse();

    public abstract boolean hasBody();

    public abstract HttpMethod method();

    public abstract String buildPath();

    public abstract ApiName apiName();

}
