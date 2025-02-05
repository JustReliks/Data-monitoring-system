package ru.spbstu.rakitin.commonstarter.admin.aspect;

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
import ru.spbstu.rakitin.commonstarter.admin.auth.SecurityUserDetails;

import java.util.*;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    private final LoggingParamsStorage loggingParamsStorage;

    public LoggingAspect(LoggingParamsStorage loggingParamsStorage) {
        this.loggingParamsStorage = loggingParamsStorage;
    }

    @Pointcut(value = "@annotation(logControllerAnnotation) && args(authentication,..)")
    private void loggingControllerPointcut(LogControllerAnnotation logControllerAnnotation, Authentication authentication) {

    }

    //    @Around(value = "@annotation(logControllerAnnotation) && args(authentication,..)")
    @Around(value = "loggingControllerPointcut(logAnnotation, authentication)")
    public Object logController(
            ProceedingJoinPoint joinPoint,
            LogControllerAnnotation logAnnotation,
            Authentication authentication)
            throws Throwable {
        LoggingParams loggingParam = buildLoggingParams(joinPoint, authentication);
        loggingParamsStorage.setLoggingParams(loggingParam);
        Level logLevel = logAnnotation.debug() ? Level.DEBUG : Level.INFO;
        log.atLevel(logLevel).log("[{}] [{}] method call was initiated with args {}. User: [{}]",
                loggingParam.getUuid(), loggingParam.getMethod(),
                loggingParam.getParamToValue(), loggingParam.getUsername().orElse(null));
        Object result = joinPoint.proceed(joinPoint.getArgs());
        log.atLevel(logLevel).log("[{}] [{}] method call was completed with args {}. User: [{}]",
                loggingParam.getUuid(), loggingParam.getMethod(),
                loggingParam.getParamToValue(), loggingParam.getUsername().orElse(null));

        return result;
    }

    @AfterThrowing(value = "loggingControllerPointcut(logAnnotation, authentication)", throwing = "exception")
    public void logControllerAfterThrowing(
            JoinPoint joinPoint,
            LogControllerAnnotation logAnnotation,
            Authentication authentication, Throwable exception) {
        LoggingParams loggingParam = loggingParamsStorage.getLoggingParams();

        log.warn("[{}] [{}] method call was completed with exception [{}] with args {}. User: [{}]",
                loggingParam.getUuid(), loggingParam.getMethod(), exception,
                loggingParam.getParamToValue(), loggingParam.getUsername().orElse(null));

    }

    private LoggingParams buildLoggingParams(JoinPoint joinPoint, Authentication authentication) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Optional<String> username = Optional.ofNullable(authentication)
                .map(auth -> (SecurityUserDetails) auth.getPrincipal())
                .map(SecurityUserDetails::getUsername);
        String method = joinPoint.getSignature().toShortString();
        String[] parameterNames = methodSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        Map<String, String> paramToValue = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Authentication) {
                continue;
            }
            paramToValue.put(parameterNames[i], Objects.toString(args[i]));
        }
        return LoggingParams.builder()
                .uuid(UUID.randomUUID().toString())
                .method(method)
                .paramToValue(paramToValue)
                .username(username).build();
    }

    @Data
    @Builder
    public static class LoggingParams {

        private String uuid;
        private String method;
        private Map<String, String> paramToValue;
        private Optional<String> username;

    }

}
