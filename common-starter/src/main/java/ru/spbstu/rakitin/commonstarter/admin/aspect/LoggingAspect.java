package ru.spbstu.rakitin.commonstarter.admin.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.event.Level;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.spbstu.rakitin.commonstarter.admin.auth.SecurityUserDetails;

import java.util.*;
import java.util.function.Function;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    protected enum MethodExecutionStage {
        INITIATE(loggingParams -> "method call was initiated"),
        COMPLETED_SUCCESSFULLY(loggingParams -> "method call was completed"),
        COMPLETED_EXCEPTIONALLY(loggingParams -> String.format("method call was completed exceptionally. Exception: [%s]",
                loggingParams.getException().orElse(null)));

        private final Function<LoggingParams, String> logBodyGenerator;

        MethodExecutionStage(Function<LoggingParams, String> logBodyGenerator) {
            this.logBodyGenerator = logBodyGenerator;
        }

        public String buildLog(LoggingParams loggingParams) {
            StringBuilder builder = new StringBuilder(String.format("[%s] [%s] %s. Args: %s User: [%s]. ",
                    loggingParams.getUuid(),
                    loggingParams.getMethod(),
                    logBodyGenerator.apply(loggingParams),
                    loggingParams.getParamToValue(),
                    loggingParams.getUsername().orElse(null)));

            if (loggingParams.getRequestContext().isPresent()) {
                RequestContext requestContext = loggingParams.getRequestContext().get();
                builder.append(String.format("Request context: [method: %s, url: %s, remoteHost: %s, remotePort: %s].",
                        requestContext.getMethod(),
                        requestContext.getUrl(),
                        requestContext.getRemoteHost(),
                        requestContext.getRemotePort()));
            }
            if (isCompletionStage()) {
                builder.append(String.format(" Execution time: %s millis.", System.currentTimeMillis() - loggingParams.getMethodStartTime()));
            }

            return builder.toString();
        }

        private boolean isCompletionStage() {
            return this == COMPLETED_EXCEPTIONALLY || this == COMPLETED_SUCCESSFULLY;
        }
    }

    private final LoggingParamsStorage loggingParamsStorage;

    public LoggingAspect(LoggingParamsStorage loggingParamsStorage) {
        this.loggingParamsStorage = loggingParamsStorage;
    }

    @Pointcut(value = "@annotation(logController)")
    private void loggingControllerPointcut(LogController logController) {

    }

    @Around(value = "loggingControllerPointcut(logAnnotation)")
    public Object logController(
            ProceedingJoinPoint joinPoint,
            LogController logAnnotation)
            throws Throwable {
        long startBuildLoggingParams = System.currentTimeMillis();
        LoggingParams loggingParam = buildLoggingParams(joinPoint);
        log.debug("[{}] Logging params build for {} millis", loggingParam.getUuid(), System.currentTimeMillis() - startBuildLoggingParams);
        loggingParamsStorage.setLoggingParams(loggingParam);
        Level logLevel = logAnnotation.debug() ? Level.DEBUG : Level.INFO;
        log.atLevel(logLevel).log(MethodExecutionStage.INITIATE.buildLog(loggingParam));
        Object result = joinPoint.proceed(joinPoint.getArgs());
        log.atLevel(logLevel).log(MethodExecutionStage.COMPLETED_SUCCESSFULLY.buildLog(loggingParam));

        return result;
    }

    @AfterThrowing(value = "loggingControllerPointcut(logAnnotation)", throwing = "exception")
    public void logControllerAfterThrowing(
            JoinPoint joinPoint,
            LogController logAnnotation, Throwable exception) {
        LoggingParams loggingParam = loggingParamsStorage.getLoggingParams();
        loggingParam.setException(Optional.of(exception));

        log.warn("[{}] [{}] method call was completed with exception [{}] with args {}. User: [{}]. Request context: [{}]",
                loggingParam.getUuid(), loggingParam.getMethod(), exception,
                loggingParam.getParamToValue(), loggingParam.getUsername().orElse(null),
                loggingParam.getRequestContext().orElse(null));

    }

    private LoggingParams buildLoggingParams(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Optional<String> username = Optional.empty();
        String method = joinPoint.getSignature().toShortString();
        String[] parameterNames = methodSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        Map<String, String> paramToValue = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Authentication && username.isEmpty()) {
                username = Optional.of(args[i])
                        .map(Authentication.class::cast)
                        .map(auth -> (SecurityUserDetails) auth.getPrincipal())
                        .map(SecurityUserDetails::getUsername);
                continue;
            }
            if (!methodSignature.getMethod().getParameters()[i].isAnnotationPresent(ExcludeFromLog.class)) {
                paramToValue.put(parameterNames[i], Objects.toString(args[i]));
            }
        }
        return LoggingParams.builder()
                .uuid(UUID.randomUUID().toString())
                .method(method)
                .paramToValue(paramToValue)
                .username(username)
                .methodStartTime(System.currentTimeMillis())
                .requestContext(buildRequestContext()).build();
    }

    public Optional<RequestContext> buildRequestContext() {
        Optional<HttpServletRequest> currentHttpRequest = getCurrentHttpRequest();
        if (currentHttpRequest.isEmpty()) return Optional.empty();
        HttpServletRequest httpServletRequest = currentHttpRequest.get();
        return Optional.of(RequestContext.builder()
                .method(httpServletRequest.getMethod())
                .url(httpServletRequest.getRequestURL().toString())
                .remoteHost(httpServletRequest.getRemoteHost())
                .remotePort(httpServletRequest.getRemotePort())
                .build());
    }

    private Optional<HttpServletRequest> getCurrentHttpRequest() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest);
    }

    @Data
    @Builder
    public static class LoggingParams {

        private String uuid;
        private String method;
        private Map<String, String> paramToValue;
        private Optional<String> username;
        private Optional<Throwable> exception;
        private Optional<RequestContext> requestContext;
        private long methodStartTime;

    }

    @Data
    @Builder
    private static class RequestContext {
        private String method;
        private String url;
        private String remoteHost;
        private int remotePort;
    }


}
