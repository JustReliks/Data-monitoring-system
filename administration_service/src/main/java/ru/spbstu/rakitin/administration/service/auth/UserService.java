package ru.spbstu.rakitin.administration.service.auth;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.spbstu.rakitin.administration.exceptions.UserAlreadyExistsException;
import ru.spbstu.rakitin.administration.exceptions.UserNotFoundException;
import ru.spbstu.rakitin.administration.model.User;

public interface UserService extends UserDetailsService {

    Long register(User user) throws UserAlreadyExistsException;

    void delete(Long id) throws UserNotFoundException;

    User loadUserByUsername(String username);
    User findUserById(long id) throws UserNotFoundException;

}
