package ru.spbstu.rakitin.commonstarter.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminManager {

    private final AdminRequestFactory adminRequestFactory;

    private static final String CHECK_PERMISSION = "/api/v1/admin/permission/user/%s/project/%s/check/%s";

    public boolean hasUserPermissionForProject(long userId, long projectId, PermissionTypeEnum permission) {
        return adminRequestFactory.doGet(String.format(CHECK_PERMISSION, userId, projectId, permission), Boolean.class);
    }

    public boolean canUserDoActionInProject(long userId, long projectId, PermissionTypeEnum permission) {
        if (permission == PermissionTypeEnum.CREATOR) {
            return hasUserPermissionForProject(userId, projectId, permission);
        }
        return hasUserPermissionForProject(userId, projectId, permission) || hasUserPermissionForProject(userId, projectId, PermissionTypeEnum.CREATOR);
    }

}
