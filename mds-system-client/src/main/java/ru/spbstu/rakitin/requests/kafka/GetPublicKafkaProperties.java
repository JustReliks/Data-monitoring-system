package ru.spbstu.rakitin.requests.kafka;

import org.springframework.http.HttpMethod;
import ru.spbstu.rakitin.client.ApiName;
import ru.spbstu.rakitin.client.MdsRequest;

import java.util.LinkedHashMap;

public class GetPublicKafkaProperties extends MdsRequest<Void, LinkedHashMap<String, String>> {
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
        return "/properties";
    }

    @Override
    public ApiName apiName() {
        return ApiName.KAFKA;
    }
}
