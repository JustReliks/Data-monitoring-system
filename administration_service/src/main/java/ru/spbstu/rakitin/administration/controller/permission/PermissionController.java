package ru.spbstu.rakitin.administration.controller.permission;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.rakitin.commonentites.model.PermissionTypeEnum;
import ru.spbstu.rakitin.administration.service.auth.PermissionService;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/permission")
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    public List<PermissionTypeEnum> getAllPermissions() {
        return Arrays.stream(PermissionTypeEnum.values()).toList();
    }

    @GetMapping("/user/{userId}/project/{projectId}/check/any")
    public boolean hasUserAnyPermissionForProject(@PathVariable long userId, @PathVariable long projectId) {
        return permissionService.hasUserAnyPermissionForProject(userId, projectId);
    }

    @GetMapping("/user/{userId}/project/{projectId}/check/{permission}")
    public boolean hasUserPermissionForProject(@PathVariable long userId,
                                                  @PathVariable long projectId,
                                                  @PathVariable PermissionTypeEnum permission) {
        return permissionService.hasUserPermissionForProject(userId, projectId, permission);
    }

}
