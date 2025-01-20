package ru.spbstu.rakitin.administration.controller.permission;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.rakitin.administration.service.auth.PermissionService;
import ru.spbstu.rakitin.administration.model.PermissionTypeEnum;

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

    @GetMapping("/{userId}/{projectId}/any")
    public boolean hasUserAnyPermissionForProject(@PathVariable long userId, @PathVariable long projectId) {
        return permissionService.hasUserAnyPermissionForProject(userId, projectId);
    }

}
