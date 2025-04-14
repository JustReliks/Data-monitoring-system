package ru.spbstu.rakitin.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import ru.spbstu.rakitin.dto.ErrorResponse;
import ru.spbstu.rakitin.dto.MapJson;

import java.util.Optional;

@Getter
@Setter
public class MdsResponse<T> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Optional<T> response;
    private boolean success;
    private Optional<Exception> exception;
    private int statusCode;
    private ErrorResponse error;


    public MapJson getAsMap() {
        return MAPPER.convertValue(response, MapJson.class);
    }

}
