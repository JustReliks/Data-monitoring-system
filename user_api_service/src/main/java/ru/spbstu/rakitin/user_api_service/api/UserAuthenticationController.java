package ru.spbstu.rakitin.user_api_service.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.rakitin.commonstarter.admin.AdminRequestFactory;
import ru.spbstu.rakitin.commonstarter.dto.AuthUserDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserAuthenticationController {

    private final AdminRequestFactory adminRequestFactory;
    //private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public String login(@RequestBody AuthUserDto authUserDto) {
        //authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authUserDto.getUsername(), authUserDto.getPassword()));

        return adminRequestFactory.doPost("/api/v1/admin/user/login", authUserDto, String.class);
    }

    @GetMapping("/hello")
    public String helloWorld(Authentication authentication) {
        System.out.println(authentication);
        return "Hello World!";
    }

}
