package ru.spbstu.rakitin.administration.service.admin;

import ru.spbstu.rakitin.administration.exceptions.AdminAlreadyExistsException;
import ru.spbstu.rakitin.administration.exceptions.AdminNotFoundException;

public interface AdminService {

    String isTokenValidAndReturnAdmin(String token);

    String createAdmin(String username) throws AdminAlreadyExistsException;

    void deleteAdmin(String username) throws AdminNotFoundException;

    String updateToken(String username, String oldToken) throws AdminNotFoundException;

}
