package ru.spbstu.rakitin.administration.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.administration.exceptions.AuthenticationException;
import ru.spbstu.rakitin.administration.exceptions.UserNotFoundException;
import ru.spbstu.rakitin.administration.model.User;
import ru.spbstu.rakitin.administration.service.auth.UserService;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;

    public User authenticateUser(String username, String password) throws UserNotFoundException, AuthenticationException {
        User user = userService.findUserByUsername(username);
        if (user.getPassword().equals(password)) {
            return user;
        }
        throw new AuthenticationException("Unable to authenticate user " + username);
    }

}
