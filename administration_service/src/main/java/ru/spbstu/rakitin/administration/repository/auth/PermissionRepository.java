package ru.spbstu.rakitin.administration.repository.auth;

import ru.spbstu.rakitin.commonentites.model.Permission;
import ru.spbstu.rakitin.commonentites.model.PermissionTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    List<Permission> findAllByUserId(Long userId);

    boolean existsPermissionByUser_IdAndProject_IdAndPermission(Long userId, Long projectId, PermissionTypeEnum permissionTypeEnum);
    boolean existsPermissionByUser_IdAndProject_Id(Long userId, Long projectId);
}
