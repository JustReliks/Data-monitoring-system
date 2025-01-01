package ru.spbstu.rakitin.administration.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminWithTokenDto  {

    @NotBlank(message = "Name is mandatory")
    private String username;

}
