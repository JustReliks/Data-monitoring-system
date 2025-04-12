package ru.spbstu.rakitin;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.spbstu.rakitin.exception.MdsRequestException;
import ru.spbstu.rakitin.requests.LoginRequest;

import java.net.ConnectException;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
@Slf4j
public class MdsClient {

    private final AuthProperties authProperties;
    private final RestTemplate restTemplate;
    private final RequestProperties requestProperties;
    private final String baseUrl;
    private final ExecutorService executor;

    public MdsClient(AuthProperties authProperties, RequestProperties requestProperties) {
        this.authProperties = authProperties;
        this.requestProperties = requestProperties;
        this.baseUrl = requestProperties.getBaseUrl();
        this.restTemplate = new RestTemplate();
        executor = Executors.newFixedThreadPool(requestProperties.getThreadsCount());
    }

    @SneakyThrows
    public <T, R> MdsResponse<R> sendRequest(MdsRequest<T, R> request) {
        String fullPath = baseUrl + request.buildFullPath();
        HttpHeaders headers = new HttpHeaders();
        if (request.needAuthentication()) {
            MdsResponse<String> response = sendRequest(LoginRequest.builder()
                    .username(authProperties.getUsername())
                    .password(authProperties.getPassword()).build());
            headers.add("Authorization", String.format("Bearer %s", response.getResponse().get()));
        }
        HttpEntity<?> entity = new HttpEntity<>(null, headers);
        if (request.hasBody()) {
            entity = new HttpEntity<>(request.getBody());
        }

        MdsResponse<R> mdsResponse = executor.submit(new RetryRequestSender<>(
                request,
                requestProperties.getRetryCount(),
                requestProperties.getRetryDelayMs(),
                fullPath,
                entity,
                restTemplate
        )).get();
        if (!mdsResponse.isSuccess() && mdsResponse.getException().isPresent()) {
            throw new MdsRequestException(String.format("Unable to execute request %s", fullPath), mdsResponse.getException().get());
        }
        return mdsResponse;
    }


    private record RetryRequestSender<T, R>(MdsRequest<T, R> request, long maxAttempts, long delay, String fullPath,
                                            HttpEntity<?> entity,
                                            RestTemplate restTemplate) implements Callable<MdsResponse<R>> {

        public static final ParameterizedTypeReference<Void> VOID_RESPONSE_TYPE = new ParameterizedTypeReference<>() {
        };

        @Override
        public MdsResponse<R> call() {
            RetryContext retryContext = new RetryContext();
            MdsResponse<R> response = null;


            while (response == null || (!response.isSuccess() && retryContext.getAttempts() != maxAttempts)) {
                log.info("Send request {}. Attempt: {}/{}", fullPath, retryContext.getAttempts() + 1, maxAttempts);
                response = this.sendRequest(request, fullPath, entity, retryContext);
                if (!response.isSuccess() && response.getException().stream().anyMatch(e -> e.getCause() instanceof ConnectException)) {
                    log.error("Unable to connect to server. Retrying in {} ms...", delay);
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }
            return response;

        }

        private MdsResponse<R> sendRequest(MdsRequest<T, R> request, String fullPath, HttpEntity<?> entity, RetryContext retryContext) {
            retryContext.setAttempts(retryContext.getAttempts() + 1);
            MdsResponse<R> rMdsResponse = new MdsResponse<>();
            try {
                ResponseEntity<?> response;
                if (!request.hasResponse()) {
                    ResponseEntity<Void> exchange = restTemplate.exchange(fullPath, request.method(), entity, VOID_RESPONSE_TYPE);
                    rMdsResponse.setResponse(Optional.empty());
                    response = exchange;
                } else {
                    ResponseEntity<R> exchange = restTemplate.exchange(fullPath, request.method(), entity, request.getResponseClass());
                    rMdsResponse.setResponse(Optional.of(exchange.getBody()));
                    response = exchange;
                }
                rMdsResponse.setSuccess(true);
                rMdsResponse.setException(Optional.empty());
                rMdsResponse.setStatusCode(response.getStatusCode().value());
            } catch (Exception e) {
                rMdsResponse.setSuccess(false);
                rMdsResponse.setException(Optional.of(e));
                rMdsResponse.setResponse(Optional.empty());
                return rMdsResponse;
            }
            return rMdsResponse;
        }
    }


}
