package ru.spbstu.rakitin.administration.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateAdminResponseDto {
    @NotBlank
    private String token;
    private String information = "Save this token. You can't perform request without it.";

}
