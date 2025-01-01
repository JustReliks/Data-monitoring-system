package ru.spbstu.rakitin.commonstarter.exceptionhandling;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {

    private String message;
    private String path;
    private String error;
    private String timestamp;
    private int status;

}
