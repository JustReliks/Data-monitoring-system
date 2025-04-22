package ru.spbstu.rakitin.user_api_service.api.project;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.dto.UserProjectAccessDto;
import ru.spbstu.rakitin.dto.UserTaskAccessDto;
import ru.spbstu.rakitin.user_api_service.service.UserAccessesService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final AdminManager adminManager;
    private final UserAccessesService userAccessesService;

    public UserController(AdminManager adminManager, UserAccessesService userAccessesService) {
        this.adminManager = adminManager;

        this.userAccessesService = userAccessesService;
    }

    @GetMapping("/project/list")
    public List<UserProjectAccessDto> findAllProjectsForUserAvailable(Authentication authentication) {
        return adminManager.findAllProjectsAvailable(authentication);
    }


    @GetMapping("/access/list")
    public List<UserTaskAccessDto> accessList(Authentication authentication) {
        return userAccessesService.accessList(authentication);
    }

}
