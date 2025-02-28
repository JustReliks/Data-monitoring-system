package ru.spbstu.rakitin.fulltext_service.exception;

import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@StandardException
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class FulltextTaskInstanceNotRunningException extends Exception {
}
