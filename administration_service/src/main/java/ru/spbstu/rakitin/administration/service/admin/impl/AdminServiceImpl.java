package ru.spbstu.rakitin.administration.service.admin.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.administration.exceptions.AdminAlreadyExistsException;
import ru.spbstu.rakitin.administration.exceptions.AdminNotFoundException;
import ru.spbstu.rakitin.administration.exceptions.AdminTokenIsNotCorrectException;
import ru.spbstu.rakitin.administration.model.Admin;
import ru.spbstu.rakitin.administration.repository.admin.AdminRepository;
import ru.spbstu.rakitin.administration.service.admin.AdminService;
import ru.spbstu.rakitin.administration.service.admin.RandomTokenGeneratorService;

import java.util.Base64;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private static final String INITIAL_ADMIN_NAME = "initial_admin";

    private final AdminRepository adminRepository;
    private final RandomTokenGeneratorService randomTokenGeneratorService;


    @Override
    public String isTokenValidAndReturnAdmin(String token) {
        Optional<Admin> adminByToken = adminRepository.findAdminByToken(Base64.getEncoder().encodeToString(token.getBytes()));
        Admin admin = adminByToken.orElseThrow(AdminTokenIsNotCorrectException::new);
        if (INITIAL_ADMIN_NAME.equals(admin.getUsername())) {
            log.warn("You are using initial admin! Please, create new admin and delete initial");
        }
        return admin.getUsername();
    }

    @Override
    public String createAdmin(String username) throws AdminAlreadyExistsException {
        if (adminRepository.findAdminByUsername(username).isPresent()) {
            throw new AdminAlreadyExistsException("Admin with name " + username + " already created");
        }
        Admin admin = new Admin();
        admin.setUsername(username);
        String token = randomTokenGeneratorService.generateRandomAdminToken();
        admin.setToken(Base64.getEncoder().encodeToString(token.getBytes()));
        adminRepository.save(admin);

        return admin.getToken();
    }

    @Override
    public void deleteAdmin(String username) throws AdminNotFoundException {
        Optional<Admin> adminByUsername = adminRepository.findAdminByUsername(username);
        if (adminByUsername.isEmpty()) {
            throw new AdminNotFoundException(String.format("Admin with username %s not found!", username));
        }

        adminRepository.delete(adminByUsername.get());
    }

    @Override
    public String updateToken(String username, String oldToken) throws AdminNotFoundException {
        Optional<Admin> adminByUsername = adminRepository.findAdminByUsername(username);
        if (adminByUsername.isEmpty()) {
            throw new AdminNotFoundException(String.format("Admin with username %s not found!", username));
        }

        Admin admin = adminByUsername.get();
        if (!admin.getToken().equals(Base64.getEncoder().encodeToString(oldToken.getBytes()))) {
            throw new AdminTokenIsNotCorrectException("Old token is not correct for admin " + username);
        }

        String token = randomTokenGeneratorService.generateRandomAdminToken();
        admin.setToken(token);
        adminRepository.save(admin);

        return token;
    }
}
