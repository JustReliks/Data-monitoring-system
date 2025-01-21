package ru.spbstu.rakitin.commonstarter.admin.aspect;

import ru.spbstu.rakitin.commonstarter.admin.PermissionTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckPermission {

    PermissionTypeEnum permission() default PermissionTypeEnum.CREATOR;
    String projectIdField();
    String userIdField();

}
