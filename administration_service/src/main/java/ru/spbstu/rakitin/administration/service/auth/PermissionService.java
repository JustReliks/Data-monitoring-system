package ru.spbstu.rakitin.administration.service.auth;

import ru.spbstu.rakitin.administration.exceptions.PermissionAlreadyExistsException;
import ru.spbstu.rakitin.administration.exceptions.UserNotFoundException;
import ru.spbstu.rakitin.commonentites.model.Permission;
import ru.spbstu.rakitin.dto.PermissionTypeEnum;

import java.util.List;

public interface PermissionService {

    Long savePermission(Permission permission) throws PermissionAlreadyExistsException;
    List<Permission> findAllPermissionsForUser(Long userId);
    boolean hasUserAnyPermissionForProject(Long userId, Long projectId) throws UserNotFoundException;
    boolean hasUserPermissionForProject(Long userId, Long projectId, PermissionTypeEnum permission) throws UserNotFoundException;

}
