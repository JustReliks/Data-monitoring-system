package ru.spbstu.rakitin.commonstarter.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.spbstu.rakitin.commonstarter.admin.exception.InternalRequestException;

@Component
@Slf4j
public class AdminRequestFactory {

    private final RestTemplate adminRestTemplate;

    public AdminRequestFactory(RestTemplate adminRestTemplate) {
        this.adminRestTemplate = adminRestTemplate;
    }

    public <RESULT, BODY> RESULT sendRequest(String path, BODY body, HttpMethod method, Class<RESULT> responseClass, Object... uriVariables) {
        try {
            if (method == HttpMethod.GET) {
                if (body != null) {
                    log.warn("The request body for the GET method was set. It will be ignored.");
                }
                ResponseEntity<RESULT> response = adminRestTemplate.getForEntity(path, responseClass, uriVariables);
                return response.getBody();
            }
            ResponseEntity<RESULT> response = adminRestTemplate.exchange(path, method, new HttpEntity<>(body), responseClass, uriVariables);
            return response.getBody();
        } catch (Exception e) {
            throw new InternalRequestException(String.format("Exception during request to %s. Message: %s", path, e.getMessage()), e);
        }
    }

    public <BODY> HttpStatusCode sendRequest(String path, BODY body, HttpMethod method, Object... uriVariables) {
        ResponseEntity<Void> response = adminRestTemplate.exchange(path, method, new HttpEntity<>(body), Void.TYPE, uriVariables);
        return response.getStatusCode();
    }

    public <RESULT, BODY> RESULT doPost(String uri, BODY body, Class<RESULT> responseClass) {
        return sendRequest(uri, body, HttpMethod.POST, responseClass);
    }

    public <RESULT> RESULT doGet(String uri, Class<RESULT> responseClass) {
        return sendRequest(uri, null, HttpMethod.GET, responseClass);
    }

    public <RESULT, BODY> RESULT doPut(String uri, BODY body, Class<RESULT> responseClass) {
        return sendRequest(uri, body, HttpMethod.PUT, responseClass);
    }

    public <RESULT, BODY> RESULT doDelete(String uri, BODY body, Class<RESULT> responseClass) {
        return sendRequest(uri, body, HttpMethod.DELETE, responseClass);
    }


}
