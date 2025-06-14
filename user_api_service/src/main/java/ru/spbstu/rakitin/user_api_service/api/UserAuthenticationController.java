package ru.spbstu.rakitin.user_api_service.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.commonstarter.admin.aspect.ExcludeFromLog;
import ru.spbstu.rakitin.commonstarter.admin.aspect.LogController;
import ru.spbstu.rakitin.dto.AuthUserDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(name = "13. Авторизация пользователя")
public class UserAuthenticationController {

    private final AdminManager adminManager;

    @PostMapping("/login")
    @LogController
    @Operation(description = "Авторизация пользователя")
    public String login(@RequestBody AuthUserDto authUserDto) {
        return adminManager.login(authUserDto);
    }

}
//{
//        "name": "books",
//        "projectId": 3,
//        "topicId": 5,
//        "schema": {
//        "fields": [
//        {
//        "fieldName": "libraryId",
//        "fieldType": "LONG",
//        "subType": null
//        },
//        {
//        "fieldName": "description",
//        "fieldType": "STRING",
//        "subType": null
//        },
//        {
//        "fieldName": "title",
//        "fieldType": "STRING",
//        "subType": null
//        }
//        ],
//        "timestampField": {
//        "fieldName": "timestamp",
//        "useInsertionDate": true
//        },
//        "filterExpression": null
//        },
//        "replicationFactor": 1,
//        "shardsCount": 1
//        }