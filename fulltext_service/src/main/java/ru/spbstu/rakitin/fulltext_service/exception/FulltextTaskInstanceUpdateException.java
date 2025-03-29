package ru.spbstu.rakitin.fulltext_service.exception;

import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@StandardException
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FulltextTaskInstanceUpdateException extends Exception {
}
