package ru.spbstu.rakitin;

import lombok.Getter;
import lombok.Setter;
import ru.spbstu.rakitin.dto.ErrorResponse;

import java.util.Optional;

@Getter
@Setter
public class MdsResponse<T> {

    private Optional<T> response;
    private boolean success;
    private Optional<Exception> exception;
    private int statusCode;
    private ErrorResponse error;

}
