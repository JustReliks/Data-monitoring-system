package ru.spbstu.rakitin.administration.service.auth;

import ru.spbstu.rakitin.administration.exceptions.PermissionAlreadyExistsException;
import ru.spbstu.rakitin.administration.model.Permission;

import java.util.List;

public interface PermissionService {

    Long savePermission(Permission permission) throws PermissionAlreadyExistsException;
    List<Permission> findAllPermissionsForUser(Long userId);

}
