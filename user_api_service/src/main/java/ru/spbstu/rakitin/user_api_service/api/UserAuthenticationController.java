package ru.spbstu.rakitin.user_api_service.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.commonstarter.admin.aspect.ExcludeFromLog;
import ru.spbstu.rakitin.commonstarter.admin.aspect.LogController;
import ru.spbstu.rakitin.dto.AuthUserDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(name = "1. Авторизация пользователя")
public class UserAuthenticationController {

    private final AdminManager adminManager;

    @PostMapping("/login")
    @LogController
    @Operation(description = "Логин пользователя")
    public String login(@RequestBody AuthUserDto authUserDto) {
        return adminManager.login(authUserDto);
    }

    @GetMapping("/hello/{test}")
    @LogController()
    @Operation(hidden = true)
    public String helloWorld(Authentication authentication, @PathVariable(name = "test") String test, @ExcludeFromLog @RequestParam(name = "test2", required = false) String test2) {
        return "Hello World!" + " " + test;
    }

}
