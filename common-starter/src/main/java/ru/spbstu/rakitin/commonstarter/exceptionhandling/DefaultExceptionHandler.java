package ru.spbstu.rakitin.commonstarter.exceptionhandling;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import ru.spbstu.rakitin.commonstarter.admin.exception.InternalRequestException;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
@Slf4j
public class DefaultExceptionHandler extends DefaultErrorAttributes {

    private final static Pattern INTERNAL_EXCEPTION_PATTERN = Pattern.compile("\\d{3} : \"(.*)\"");

    private final Gson exceptionHandlerGson;

    public DefaultExceptionHandler() {
        this.exceptionHandlerGson = new GsonBuilder().create();
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception, ServletWebRequest webRequest) {
        ResponseStatus annotation = exception.getClass().getAnnotation(ResponseStatus.class);
        ErrorResponse.ErrorResponseBuilder builder = ErrorResponse.builder()
                .path(webRequest.getRequest().getRequestURL().toString())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(exception.getMessage())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .timestamp(new Date().toString());
        if (annotation != null) {
            builder.status(annotation.value().value())
                    .error(annotation.value().getReasonPhrase());
        }

        ErrorResponse errorResponse = builder.build();
        log.warn(ExceptionUtils.getStackTrace(exception));
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(InternalRequestException.class)
    public ResponseEntity<ErrorResponse> handleInternalRequestException(InternalRequestException exception, ServletWebRequest webRequest) {
        log.error(ExceptionUtils.getStackTrace(exception));
        try {
            ErrorResponse errorResponse = extractErrorResponseFromInteralRequestException(exception, webRequest);
            return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
        } catch (Exception e) {
            log.warn("Unable to parse body of internal request exception to Error response: {}", exception.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                    .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .message("Unable to execute internal request: " + exception.getMessage())
                    .path(webRequest.getRequest().getRequestURL().toString())
                    .timestamp(new Date().toString())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value()).build());
        }
    }

    private ErrorResponse extractErrorResponseFromInteralRequestException(InternalRequestException exception, ServletWebRequest webRequest) {
        String message = exception.getCause().getMessage();
        Matcher matcher = INTERNAL_EXCEPTION_PATTERN.matcher(message);
        if (matcher.matches()) {
            message = matcher.group(1);
        }
        try {
            return exceptionHandlerGson.fromJson(message, ErrorResponse.class);
        } catch (Exception e) {
            return ErrorResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .message("Unexpected error: " + exception.getCause().getMessage())
                    .timestamp(new Date().toString())
                    .path(webRequest.getRequest().getRequestURL().toString()).build();
        }

    }

}
