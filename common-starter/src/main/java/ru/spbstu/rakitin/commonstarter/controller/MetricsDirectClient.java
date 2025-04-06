package ru.spbstu.rakitin.commonstarter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.spbstu.rakitin.commonstarter.discovery.InnerServiceRequestFactory;
import ru.spbstu.rakitin.commonstarter.discovery.ParametrizedTypes;

import java.util.Optional;

@Component
public class MetricsDirectClient {

    private InnerServiceRequestFactory innerServiceRequestFactory;

    @Autowired
    public void setInnerServiceRequestFactory(@Lazy InnerServiceRequestFactory innerServiceRequestFactory) {
        this.innerServiceRequestFactory = innerServiceRequestFactory;
    }

    public Optional<Double> getCpuUsage(String serviceHost) {
        ResponseEntity<Double> doubleResponseEntity = innerServiceRequestFactory.sendRequest(serviceHost, "/metrics/cpu", HttpMethod.GET, ParametrizedTypes.DOUBLE, new Object[]{}, null);
        return Optional.ofNullable(doubleResponseEntity.getBody());
    }

}
