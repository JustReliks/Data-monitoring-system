package ru.spbstu.rakitin.administration.dto;

import lombok.Builder;
import lombok.Data;
import ru.spbstu.rakitin.dto.PermissionTypeEnum;

@Data
@Builder
public class UserPermissionDto {

    private long userId;
    private long projectId;
    private PermissionTypeEnum permission;

}
