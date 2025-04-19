package ru.spbstu.rakitin.commonstarter.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.commonentites.model.Project;
import ru.spbstu.rakitin.commonstarter.admin.auth.SecurityUserDetails;
import ru.spbstu.rakitin.dto.AuthUserDto;
import ru.spbstu.rakitin.dto.PermissionTypeEnum;
import ru.spbstu.rakitin.dto.UserProjectAccessDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminManager {

    private final AdminRequestFactory adminRequestFactory;

    private static final String FIND_PROJECTS_FOR_USER = "/api/v1/admin/user/%s/projects/list";
    private static final String CHECK_PERMISSION = "/api/v1/admin/permission/user/%s/project/%s/check/%s";
    private static final String FIND_PROJECT_BY_ID = "/api/v1/admin/internal/project/%s";
    private static final String CHECK_ANY_PERMISSION = "/user/%s/project/%s/check/any";

    public boolean hasUserPermissionForProject(long userId, long projectId, PermissionTypeEnum permission) {
        return adminRequestFactory.doGet(String.format(CHECK_PERMISSION, userId, projectId, permission), Boolean.class);
    }

    public boolean hasUserAnyPermissionForProject(long userId, long projectId) {
        return adminRequestFactory.doGet(String.format(CHECK_ANY_PERMISSION, userId, projectId), Boolean.class);
    }

    public boolean canUserDoActionInProject(long userId, long projectId, PermissionTypeEnum permission) {
        if (permission == PermissionTypeEnum.CREATOR) {
            return hasUserPermissionForProject(userId, projectId, permission);
        }
        if (permission == PermissionTypeEnum.ANY) {
            return hasUserAnyPermissionForProject(userId, projectId);
        }
        return hasUserPermissionForProject(userId, projectId, permission) || hasUserPermissionForProject(userId, projectId, PermissionTypeEnum.CREATOR);
    }

    public boolean canUserDoActionInProject(Authentication authentication, long projectId, PermissionTypeEnum permission) {
        return canUserDoActionInProject(((SecurityUserDetails) authentication.getPrincipal()).getId(), projectId, permission);
    }

    public void checkAccessThrowable(Authentication authentication, long projectId, PermissionTypeEnum permission) {
        Long id = ((SecurityUserDetails) authentication.getPrincipal()).getId();
        boolean access = canUserDoActionInProject(id, projectId, permission);
        if (!access) {
            throw new AccessDeniedException(String.format("User %s dont have access to perform this operation with project %s", id, projectId));
        }

    }

    public String login(AuthUserDto authUserDto) {
        return adminRequestFactory.doPost("/api/v1/admin/user/login", authUserDto, String.class);
    }

    public Project findProjectById(long projectId) {
        return adminRequestFactory.doGet(String.format(FIND_PROJECT_BY_ID, projectId), Project.class);
    }

    public List<UserProjectAccessDto> findAllProjectsAvailable(Authentication authentication) {
        Long id = ((SecurityUserDetails) authentication.getPrincipal()).getId();

        return adminRequestFactory.doGet(String.format(FIND_PROJECTS_FOR_USER, id), new ParameterizedTypeReference<List<UserProjectAccessDto>>() {
        });
    }
}
