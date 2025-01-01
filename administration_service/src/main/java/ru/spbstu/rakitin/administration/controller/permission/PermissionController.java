package ru.spbstu.rakitin.administration.controller.permission;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.rakitin.administration.model.PermissionTypeEnum;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/permission")
public class PermissionController {

    @GetMapping
    public List<PermissionTypeEnum> getAllPermissions() {
        return Arrays.stream(PermissionTypeEnum.values()).toList();
    }

}
