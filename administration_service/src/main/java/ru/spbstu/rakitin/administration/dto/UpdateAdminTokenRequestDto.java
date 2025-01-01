package ru.spbstu.rakitin.administration.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateAdminTokenRequestDto {

    @NotBlank(message = "Name is mandatory")
    private String username;
    private String oldToken;

}
