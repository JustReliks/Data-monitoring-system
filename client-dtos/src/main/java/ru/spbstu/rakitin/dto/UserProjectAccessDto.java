package ru.spbstu.rakitin.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UserProjectAccessDto {

    private ProjectDto project;
    private Set<PermissionTypeEnum> permissions;

}
