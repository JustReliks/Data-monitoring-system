package ru.spbstu.rakitin.administration.service.auth;

import ru.spbstu.rakitin.administration.exceptions.ProjectNotFoundException;
import ru.spbstu.rakitin.administration.exceptions.UserAlreadyExistsException;
import ru.spbstu.rakitin.administration.exceptions.UserNotFoundException;
import ru.spbstu.rakitin.administration.model.Project;
import ru.spbstu.rakitin.administration.model.User;

public interface UserService {

    Long register(User user) throws UserAlreadyExistsException;

    void delete(Long id) throws UserNotFoundException;

    User findUserByUsername(String username) throws UserNotFoundException;
    User findUserById(long id) throws UserNotFoundException;

}
