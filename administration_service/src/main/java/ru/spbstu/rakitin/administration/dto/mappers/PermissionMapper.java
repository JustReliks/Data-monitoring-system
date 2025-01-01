package ru.spbstu.rakitin.administration.dto.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.spbstu.rakitin.administration.dto.UserPermissionDto;
import ru.spbstu.rakitin.administration.exceptions.ProjectNotFoundException;
import ru.spbstu.rakitin.administration.exceptions.UserNotFoundException;
import ru.spbstu.rakitin.administration.model.Permission;
import ru.spbstu.rakitin.administration.service.auth.ProjectService;
import ru.spbstu.rakitin.administration.service.auth.UserService;

@Component
@RequiredArgsConstructor
public class PermissionMapper {

    private final UserService userService;
    private final ProjectService projectService;

    public Permission fromUserPermissionDtoToPermission(UserPermissionDto userPermissionDto) throws UserNotFoundException, ProjectNotFoundException {
        return Permission.builder()
                .user(userService.findUserById(userPermissionDto.getUserId()))
                .project(projectService.findProjectById(userPermissionDto.getProjectId()))
                .permission(userPermissionDto.getPermission())
                .build();
    }

    public UserPermissionDto fromPermissionToUserPermissionDto(Permission permission) {
        return UserPermissionDto.builder().userId(permission.getUser().getId())
                .projectId(permission.getProject().getId())
                .permission(permission.getPermission())
                .build();
    }


}
