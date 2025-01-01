package ru.spbstu.rakitin.user_api_service.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.rakitin.commonstarter.admin.AdminRequestFactory;
import ru.spbstu.rakitin.commonstarter.dto.AuthUserDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserAuthenticationService {

    private final AdminRequestFactory adminRequestFactory;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public String login(@RequestBody AuthUserDto authUserDto) {
        String token = adminRequestFactory.doPost("/api/v1/admin/user/login", authUserDto, String.class);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authUserDto.getUsername(), authUserDto.getPassword()));

        return token;
    }

}
