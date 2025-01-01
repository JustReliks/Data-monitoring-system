package ru.spbstu.rakitin.commonstarter.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthUserDto {
    @NotBlank(message = "Name is mandatory")
    private String username;
    @NotBlank(message = "Password is mandatory")
    private transient String password;

}
