package ru.spbstu.rakitin.administration.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.administration.exceptions.AuthenticationException;
import ru.spbstu.rakitin.administration.model.User;
import ru.spbstu.rakitin.administration.service.auth.UserService;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public User authenticateUser(String username, String password) throws AuthenticationException {
        User user = userService.loadUserByUsername(username);
        if (passwordEncoder.encode(password).equals(user.getPassword())) {
            return user;
        }
        throw new AuthenticationException("Unable to authenticate user " + username + "! Username or password is incorrect!");
    }

}
