package ru.spbstu.rakitin.administration.dto.mappers;

import ru.spbstu.rakitin.administration.model.User;
import ru.spbstu.rakitin.commonstarter.dto.AuthUserDto;

public class UserMapper {

    public static User authUserToUser(AuthUserDto authUserDto) {
        return User.builder()
                .username(authUserDto.getUsername())
                .password(authUserDto.getPassword())
                .build();
    }

}
