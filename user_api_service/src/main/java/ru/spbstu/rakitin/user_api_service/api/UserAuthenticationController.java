package ru.spbstu.rakitin.user_api_service.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.rakitin.commonstarter.admin.AdminRequestFactory;
import ru.spbstu.rakitin.commonstarter.admin.aspect.ExcludeFromLog;
import ru.spbstu.rakitin.commonstarter.admin.aspect.LogController;
import ru.spbstu.rakitin.commonstarter.dto.AuthUserDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserAuthenticationController {

    private final AdminRequestFactory adminRequestFactory;

    @PostMapping("/login")
    @LogController
    public String login(@RequestBody AuthUserDto authUserDto) {
        return adminRequestFactory.doPost("/api/v1/admin/user/login", authUserDto, String.class);
    }

    @GetMapping("/hello/{test}")
    @LogController()
    public String helloWorld(Authentication authentication, @PathVariable(name = "test") String test, @ExcludeFromLog @RequestParam(name = "test2", required = false) String test2) {
//        throw new RuntimeException("test");
        return "Hello World!" + " " + test;
    }

}
