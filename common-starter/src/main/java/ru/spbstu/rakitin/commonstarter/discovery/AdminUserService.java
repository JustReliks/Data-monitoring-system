package ru.spbstu.rakitin.commonstarter.discovery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.commonstarter.dto.AuthUserDto;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final AdminManager adminManager;
    private final AdminUserConfiguration adminUserConfiguration;

    public String getJwt() {
        return adminManager.login(AuthUserDto.builder()
                .password(adminUserConfiguration.getPassword())
                .username(adminUserConfiguration.getUsername()).build());
    }

}
