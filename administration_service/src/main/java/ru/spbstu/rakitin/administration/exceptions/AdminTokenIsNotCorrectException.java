package ru.spbstu.rakitin.administration.exceptions;

import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
@StandardException
public class AdminTokenIsNotCorrectException extends RuntimeException {
}
