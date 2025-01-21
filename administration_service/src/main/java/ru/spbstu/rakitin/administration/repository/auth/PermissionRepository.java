package ru.spbstu.rakitin.administration.repository.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.spbstu.rakitin.administration.model.Permission;
import ru.spbstu.rakitin.commonstarter.admin.PermissionTypeEnum;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    List<Permission> findAllByUserId(Long userId);

    boolean existsPermissionByUser_IdAndProject_IdAndPermission(Long userId, Long projectId, PermissionTypeEnum permissionTypeEnum);
    boolean existsPermissionByUser_IdAndProject_Id(Long userId, Long projectId);
}
