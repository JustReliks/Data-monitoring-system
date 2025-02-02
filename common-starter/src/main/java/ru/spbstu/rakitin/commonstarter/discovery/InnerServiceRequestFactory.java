package ru.spbstu.rakitin.commonstarter.discovery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.spbstu.rakitin.commonstarter.admin.auth.SecurityUserDetails;
import ru.spbstu.rakitin.commonstarter.admin.exception.InternalRequestException;

import java.util.Optional;

@Component
@Slf4j
public class InnerServiceRequestFactory {

    private final DiscoveryService discoveryService;
    private final RestTemplate restTemplate;

    public InnerServiceRequestFactory(DiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
        this.restTemplate = new RestTemplateBuilder().build();
    }

    public <RESULT, BODY> RESULT sendRequest(ServiceName serviceName, String path, BODY body, HttpMethod method, Class<RESULT> responseClass, Authentication authentication, Object... uriVariables) {
        String host = discoveryService.findServiceHost(serviceName);
        path = host + path;
        HttpEntity<BODY> requestEntity = createHttpEntity(body, authentication);
        try {
            if (method == HttpMethod.GET) {
                if (requestEntity.hasBody()) {
                    log.warn("The request body for the GET method was set. It will be ignored.");
                }
            }
            ResponseEntity<RESULT> response = restTemplate.exchange(path, method, requestEntity, responseClass, uriVariables);
            return response.getBody();
        } catch (Exception e) {
            throw new InternalRequestException(String.format("Exception during request to %s. Message: %s", path, e.getMessage()), e);
        }
    }

    public <BODY> HttpStatusCode sendRequest(ServiceName serviceName, String path, BODY body, HttpMethod method, Authentication authentication, Object... uriVariables) {
        path = discoveryService.findServiceHost(serviceName) + path;
        HttpEntity<BODY> requestEntity = createHttpEntity(body, authentication);
        ResponseEntity<Void> response = restTemplate.exchange(path, method, requestEntity, Void.TYPE, uriVariables);
        return response.getStatusCode();
    }

    public <RESULT, BODY> RESULT doPost(ServiceName serviceName, Authentication authentication, String uri, BODY body, Class<RESULT> responseClass) {
        return sendRequest(serviceName, uri, body, HttpMethod.POST, responseClass, authentication);
    }

    public <RESULT> RESULT doGet(ServiceName serviceName, Authentication authentication, String uri, Class<RESULT> responseClass) {
        return sendRequest(serviceName, uri, null, HttpMethod.GET, responseClass, authentication);
    }

    public <RESULT, BODY> RESULT doPut(ServiceName serviceName, Authentication authentication, String uri, BODY body, Class<RESULT> responseClass) {
        return sendRequest(serviceName, uri, body, HttpMethod.PUT, responseClass, authentication);
    }

    public <RESULT, BODY> RESULT doDelete(ServiceName serviceName, Authentication authentication, String uri, BODY body, Class<RESULT> responseClass) {
        return sendRequest(serviceName, uri, body, HttpMethod.DELETE, responseClass, authentication);
    }

    private <BODY> HttpEntity<BODY> createHttpEntity(BODY body, Authentication authentication) {
        MultiValueMap<String, String> headers = new HttpHeaders();

        Optional<String> tokenOptional = Optional.ofNullable(authentication).map(Authentication::getPrincipal)
                .map(SecurityUserDetails.class::cast)
                .map(SecurityUserDetails::getToken);
        tokenOptional.ifPresent(token -> headers.add("Authorization", "Bearer " + token));
        return new HttpEntity<>(body, headers);

    }
}