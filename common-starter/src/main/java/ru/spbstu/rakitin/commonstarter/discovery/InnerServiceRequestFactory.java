package ru.spbstu.rakitin.commonstarter.discovery;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.spbstu.rakitin.commonstarter.admin.auth.SecurityUserDetails;
import ru.spbstu.rakitin.commonstarter.admin.exception.InternalRequestException;
import ru.spbstu.rakitin.commonstarter.exception.ServiceNotFoundException;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Component
@Slf4j
public class InnerServiceRequestFactory {

    private final DiscoveryService discoveryService;
    private final RestTemplate restTemplate;

    public InnerServiceRequestFactory(DiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
        this.restTemplate = new RestTemplateBuilder().build();
    }

    public <RESULT, BODY, JWT> RESULT sendRequest(ServiceName serviceName, String path, BODY body, HttpMethod method, Class<RESULT> responseClass, JWT authentication, Object... uriVariables) {
        return sendRequest(serviceName, path, body, method, responseClass, getJwtTokenSupplierFromAuthentication(authentication), uriVariables);
    }

    @SneakyThrows
    public <RESULT, BODY> RESULT sendRequest(ServiceName serviceName, String path, BODY body, HttpMethod method, Class<RESULT> responseClass, Supplier<String> jwtToken, Object... uriVariables) {
        HttpEntity<BODY> requestEntity = createHttpEntity(body, jwtToken);
        try {
            if (method == HttpMethod.GET) {
                if (requestEntity.hasBody()) {
                    log.warn("The request body for the GET method was set. It will be ignored.");
                }
            }
            ResponseEntity<RESULT> response = sendRequest(serviceName, path, method, responseClass, uriVariables, requestEntity, new ArrayList<>());
            return response.getBody();
        } catch (Exception e) {
            throw new InternalRequestException(String.format("Exception during request to %s. Message: %s", path, e.getMessage()), e);
        }
    }

    private <RESULT, BODY> ResponseEntity<RESULT> sendRequest(ServiceName serviceName,
                                                              String path,
                                                              HttpMethod method,
                                                              Class<RESULT> responseClass,
                                                              Object[] uriVariables,
                                                              HttpEntity<BODY> requestEntity, List<String> blackList) throws ServiceNotFoundException {
        String serviceHost = discoveryService.findServiceHost(serviceName, blackList);
        try {
            return sendRequest(serviceHost, path, method, responseClass, uriVariables, requestEntity);
        } catch (Exception ex) {
            if (ex.getCause() instanceof ConnectException) {
                blackList.add(serviceHost);
                log.warn("Unable to connect to {}. Try next host.", serviceHost, ex);
                return sendRequest(serviceName, path, method, responseClass, uriVariables, requestEntity, blackList);
            }
            throw ex;
        }
    }

    private <RESULT, BODY> ResponseEntity<RESULT> sendRequest(String host,
                                                              String path,
                                                              HttpMethod method,
                                                              Class<RESULT> responseClass,
                                                              Object[] uriVariables,
                                                              HttpEntity<BODY> requestEntity) {
        path = host + path;
        return restTemplate.exchange(path, method, requestEntity, responseClass, uriVariables);
    }


    @SneakyThrows
    public <BODY, JWT> HttpStatusCode sendRequest(ServiceName serviceName, String path, BODY body, HttpMethod method, JWT authentication, Object... uriVariables) {
        List<String> deadServers = new ArrayList<>();
        path = discoveryService.findServiceHost(serviceName, deadServers) + path;
        HttpEntity<BODY> requestEntity = createHttpEntity(body, getJwtTokenSupplierFromAuthentication(authentication));
        ResponseEntity<Void> response = restTemplate.exchange(path, method, requestEntity, Void.TYPE, uriVariables);
        return response.getStatusCode();
    }


    public <RESULT, BODY, JWT> RESULT doPost(ServiceName serviceName, JWT authentication, String uri, BODY body, Class<RESULT> responseClass) {
        return sendRequest(serviceName, uri, body, HttpMethod.POST, responseClass, authentication);
    }

    public <RESULT, JWT> RESULT doGet(ServiceName serviceName, JWT authentication, String uri, Class<RESULT> responseClass) {
        return sendRequest(serviceName, uri, null, HttpMethod.GET, responseClass, authentication);
    }

    public <RESULT, BODY, JWT> RESULT doPut(ServiceName serviceName, JWT authentication, String uri, BODY body, Class<RESULT> responseClass) {
        return sendRequest(serviceName, uri, body, HttpMethod.PUT, responseClass, authentication);
    }

    public <RESULT, BODY, JWT> RESULT doDelete(ServiceName serviceName, JWT authentication, String uri, BODY body, Class<RESULT> responseClass) {
        return sendRequest(serviceName, uri, body, HttpMethod.DELETE, responseClass, authentication);
    }

    private <JWT> Supplier<String> getJwtTokenSupplierFromAuthentication(JWT jwt) {
        Optional<String> token;
        if (jwt instanceof Authentication authentication) {
            token = Optional.of(authentication).map(Authentication::getPrincipal)
                    .map(SecurityUserDetails.class::cast)
                    .map(SecurityUserDetails::getToken);
        } else if (jwt instanceof String jwtStr) {
            token = Optional.of(jwtStr);
        } else {
            throw new RuntimeException("Unable to get jwt token from object " + jwt);
        }

        if (token.isEmpty()) {
            return null;
        }
        return token::get;
    }

    private <BODY> HttpEntity<BODY> createHttpEntity(BODY body, Supplier<String> jwtSupplier) {
        MultiValueMap<String, String> headers = new HttpHeaders();
        if (jwtSupplier != null) {
            headers.add("Authorization", "Bearer " + jwtSupplier.get());
        }
        return new HttpEntity<>(body, headers);

    }
}