package ru.spbstu.rakitin.client;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.spbstu.rakitin.dto.TaskClientDto;
import ru.spbstu.rakitin.exception.MdsRequestException;
import ru.spbstu.rakitin.requests.LoginRequest;
import ru.spbstu.rakitin.requests.kafka.GetPublicKafkaProperties;

import java.net.ConnectException;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
@Slf4j
public class MdsClientImpl implements MdsClient {


    private final AuthProperties authProperties;
    private final RestTemplate restTemplate;
    private final RequestProperties requestProperties;
    private final String baseUrl;
    private final ExecutorService executor;
    private final KafkaProducerService kafkaProducerService;
    private final TaskResolver taskResolver;
    private final ApiKeyStorage apiKeyStorage;

    public MdsClientImpl(AuthProperties authProperties, RequestProperties requestProperties, ApiKeyStorageProperties apiKeyStorageProperties, Properties kafkaClientProperties) {
        this.apiKeyStorage = new ApiKeyStorage(apiKeyStorageProperties);
        this.authProperties = authProperties;
        this.requestProperties = requestProperties;
        this.baseUrl = requestProperties.getBaseUrl();
        this.restTemplate = new RestTemplate();
        executor = Executors.newFixedThreadPool(requestProperties.getThreadsCount());
        if (kafkaClientProperties != null) {
            this.kafkaProducerService = new KafkaProducerService(kafkaClientProperties);
        } else {
            kafkaProducerService = tryCreateKafkaProducerServiceFromWithApi();
        }
        this.taskResolver = new TaskResolverImpl(this);
    }

    private KafkaProducerService tryCreateKafkaProducerServiceFromWithApi() {
        GetPublicKafkaProperties getPublicKafkaProperties = new GetPublicKafkaProperties();
        try {
            LinkedHashMap<String, Object> mapJsonMdsResponse = sendRequest(getPublicKafkaProperties).getResponse().get();
            Properties props = new Properties();
            props.putAll(mapJsonMdsResponse);
            return new KafkaProducerService(props);
        } catch (Exception e) {
            log.error("Unable to get public kafka properties with api. KafkaProcuderService will not configured", e);
        }
        return null;
    }

    public MdsClientImpl(AuthProperties authProperties, RequestProperties requestProperties, ApiKeyStorageProperties apiKeyStorageProperties) {
        this(authProperties, requestProperties, apiKeyStorageProperties, null);
    }

    @SneakyThrows
    @Override
    public <T, R> MdsResponse<R> sendRequest(MdsRequest<T, R> request) {
        String fullPath = baseUrl + request.buildFullPath();
        HttpHeaders headers = new HttpHeaders();
        if (request.needAuthentication()) {
            String apiKey;
            if (apiKeyStorage.isValid()) {
                apiKey = apiKeyStorage.getApiKey();
            } else {
                MdsResponse<String> response = sendRequest(LoginRequest.builder()
                        .username(authProperties.getUsername())
                        .password(authProperties.getPassword()).build());
                apiKey = response.getResponse().get();
                apiKeyStorage.setApiKey(apiKey);
            }
            headers.add("Authorization", String.format("Bearer %s", apiKey));
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

    @Override
    public <T> void sendMessageToTask(TaskClientDto taskClientDto, T data) {
        if (kafkaProducerService == null) {
            throw new MdsRequestException("Kafka is not configured for this mds client");
        }

        String topic = taskResolver.resolveTopicName(taskClientDto);
        kafkaProducerService.sendDataToTopic(topic, data);
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
