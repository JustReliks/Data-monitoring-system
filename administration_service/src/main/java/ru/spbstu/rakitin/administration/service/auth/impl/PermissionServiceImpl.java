package ru.spbstu.rakitin.administration.service.auth.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.administration.exceptions.PermissionAlreadyExistsException;
import ru.spbstu.rakitin.commonentites.model.Permission;
import ru.spbstu.rakitin.commonentites.model.PermissionTypeEnum;
import ru.spbstu.rakitin.administration.repository.auth.PermissionRepository;
import ru.spbstu.rakitin.administration.service.auth.PermissionService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    public Long savePermission(Permission permission) throws PermissionAlreadyExistsException {
        if (permissionRepository.existsPermissionByUser_IdAndProject_IdAndPermission(permission.getUser().getId(), permission.getProject().getId(), permission.getPermission())) {
            throw new PermissionAlreadyExistsException(String.format("User with id %s already have permission %s in project with id %s",
                    permission.getUser().getId(),
                    permission.getPermission(),
                    permission.getProject().getId()));
        }
        return permissionRepository.save(permission).getId();
    }

    @Override
    public List<Permission> findAllPermissionsForUser(Long userId) {
        return permissionRepository.findAllByUserId(userId);
    }

    @Override
    public boolean hasUserAnyPermissionForProject(Long userId, Long projectId) {
        return permissionRepository.existsPermissionByUser_IdAndProject_Id(userId, projectId);
    }

    @Override
    public boolean hasUserPermissionForProject(Long userId, Long projectId, PermissionTypeEnum permission) {
        return permissionRepository.existsPermissionByUser_IdAndProject_IdAndPermission(userId, projectId, permission);
    }
}
