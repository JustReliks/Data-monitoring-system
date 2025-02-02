package ru.spbstu.rakitin.commonstarter.admin.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import ru.spbstu.rakitin.commonentites.model.PermissionTypeEnum;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.commonstarter.admin.auth.SecurityUserDetails;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Component
@Aspect
@RequiredArgsConstructor
public class CheckPermissionAspect {

    private final AdminManager adminManager;

    @Around("@annotation(checkPermission))")
    public Object checkPermissionForProject(ProceedingJoinPoint joinPoint, CheckPermission checkPermission) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        String projectIdField = checkPermission.projectIdField();
        String userIdField = checkPermission.userIdField();

        Long projectId = null;
        Long userId = null;

        String[] parameterNames = methodSignature.getParameterNames();
        for (int i = 0; i < parameterNames.length; i++) {
            if (projectId != null && userId != null) {
                break;
            }
            if (projectIdField.equals(parameterNames[i])) {
                projectId = extractProjectId(joinPoint, method, i);
            } else if (userIdField.equals(parameterNames[i])) {
                userId = extractUserId(joinPoint, i);
            }
        }

        if (projectId == null || userId == null) {
            throw new RuntimeException(String.format("Can't find neither project id field nor user id field in method %s in fields %s, %s",
                    method.getName(), projectIdField, userIdField));
        }
        boolean check = adminManager.canUserDoActionInProject(userId, projectId, checkPermission.permission());
        if (!check) {
            throw new AccessDeniedException(String.format("Operation in project %s is forbidden for user %s!", projectId, userId));
        }
        return joinPoint.proceed();
    }

    private static Long extractUserId(ProceedingJoinPoint joinPoint, int i) {
        Object arg = joinPoint.getArgs()[i];
        if (arg instanceof Long) {
            return (Long) arg;
        }
        if (arg instanceof Authentication) {
            return ((SecurityUserDetails) ((Authentication) arg).getPrincipal()).getId();
        }

        return null;
    }

    private static Long extractProjectId(ProceedingJoinPoint joinPoint, Method method, int i) throws NoSuchFieldException {
        Long projectId;
        Parameter parameter = method.getParameters()[i];
        if (parameter.isAnnotationPresent(ProjectIdContainer.class)) {
            ProjectIdContainer annotation = parameter.getAnnotation(ProjectIdContainer.class);
            String field = annotation.innerFieldName();
            Object fieldValue = joinPoint.getArgs()[i];
            Field declaredField = fieldValue.getClass().getDeclaredField(field);
            declaredField.setAccessible(true);
            projectId = (Long) ReflectionUtils.getField(declaredField, fieldValue);
        } else {
            projectId = (Long) joinPoint.getArgs()[i];
        }
        return projectId;
    }

}
