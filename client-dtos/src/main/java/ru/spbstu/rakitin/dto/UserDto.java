package ru.spbstu.rakitin.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserDto {

    private Long id;
    private String username;
    private String password;
    private List<String> authorities;
    private boolean isValid;
    private boolean isAdmin;

}
