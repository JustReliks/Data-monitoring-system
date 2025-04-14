package ru.spbstu.rakitin.client;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthProperties {

    private String username;
    private String password;

}
