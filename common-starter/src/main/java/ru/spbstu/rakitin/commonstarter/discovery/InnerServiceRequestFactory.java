package ru.spbstu.rakitin.commonstarter.discovery;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.spbstu.rakitin.commonstarter.admin.auth.SecurityUserDetails;
import ru.spbstu.rakitin.commonstarter.admin.exception.InternalRequestException;
import ru.spbstu.rakitin.commonstarter.configuration.InnerRequestConfiguration;
import ru.spbstu.rakitin.commonstarter.exception.ServiceNotFoundException;

import java.net.ConnectException;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Component
@Slf4j
public class InnerServiceRequestFactory {

    private final DiscoveryService discoveryService;
    private final RestTemplate restTemplate;

    public InnerServiceRequestFactory(DiscoveryService discoveryService, InnerRequestConfiguration innerRequestConfiguration) {
        this.discoveryService = discoveryService;
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        if (innerRequestConfiguration.isUseHttp2()) {
            restTemplateBuilder.requestFactory(() -> new JdkClientHttpRequestFactory(HttpClient
                    .newBuilder()
                    .version(HttpClient.Version.HTTP_2).build()));
        }
        this.restTemplate = restTemplateBuilder.build();
    }

    public <RESULT, BODY, JWT> RESULT sendRequest(ServiceName serviceName, String path, BODY body, HttpMethod method, ParameterizedTypeReference<RESULT> responseClass, JWT authentication, Object... uriVariables) {
        return sendRequest(serviceName, path, body, method, responseClass, getJwtTokenSupplierFromAuthentication(authentication), uriVariables);
    }

    @SneakyThrows
    public <RESULT, BODY> RESULT sendRequest(ServiceName serviceName, String path, BODY body, HttpMethod method, ParameterizedTypeReference<RESULT> responseClass, Supplier<String> jwtToken, Object... uriVariables) {
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
                                                              ParameterizedTypeReference<RESULT> responseClass,
                                                              Object[] uriVariables,
                                                              HttpEntity<BODY> requestEntity, List<String> blackList) throws ServiceNotFoundException {
        log.info("Searching for the service: {}", serviceName);
        String serviceHost = discoveryService.findServiceHost(serviceName, blackList);
        log.info("Found host: {}", serviceHost);
        try {
            return sendRequest(serviceHost, path, method, responseClass, uriVariables, requestEntity);
        } catch (Exception ex) {
            if (ex.getCause() instanceof ConnectException) {
                blackList.add(serviceHost);
                log.warn("Unable to connect to {}. Try next host.", serviceHost);
                return sendRequest(serviceName, path, method, responseClass, uriVariables, requestEntity, blackList);
            }
            throw ex;
        }
    }

    private <RESULT, BODY> ResponseEntity<RESULT> sendRequest(String host,
                                                              String path,
                                                              HttpMethod method,
                                                              ParameterizedTypeReference<RESULT> responseClass,
                                                              Object[] uriVariables,
                                                              HttpEntity<BODY> requestEntity) {
        path = host + path;
        return restTemplate.exchange(path, method, requestEntity, responseClass, uriVariables);
    }


    public <RESULT, BODY, JWT> RESULT doPost(ServiceName serviceName, JWT authentication, String uri, BODY body, ParameterizedTypeReference<RESULT> responseClass) {
        return sendRequest(serviceName, uri, body, HttpMethod.POST, responseClass, authentication);
    }

    public <RESULT, JWT> RESULT doGet(ServiceName serviceName, JWT authentication, String uri, ParameterizedTypeReference<RESULT> typeReference) {
        return sendRequest(serviceName, uri, null, HttpMethod.GET, typeReference, getJwtTokenSupplierFromAuthentication(authentication));
    }


    public <RESULT, BODY, JWT> RESULT doPut(ServiceName serviceName, JWT authentication, String uri, BODY body, ParameterizedTypeReference<RESULT> responseClass) {
        return sendRequest(serviceName, uri, body, HttpMethod.PUT, responseClass, authentication);
    }

    public <RESULT, BODY, JWT> RESULT doDelete(ServiceName serviceName, JWT authentication, String uri, BODY body, ParameterizedTypeReference<RESULT> responseClass) {
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