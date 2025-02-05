package ru.spbstu.rakitin.commonstarter.admin.aspect;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class LoggingParamsStorage {

    private LoggingAspect.LoggingParams loggingParams;

    public LoggingAspect.LoggingParams getLoggingParams() {
        return loggingParams;
    }

    public void setLoggingParams(LoggingAspect.LoggingParams loggingParams) {
        this.loggingParams = loggingParams;
    }
}
