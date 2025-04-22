package ru.spbstu.rakitin.user_api_service.service;

import org.springframework.security.core.Authentication;
import ru.spbstu.rakitin.dto.UserTaskAccessDto;

import java.util.List;

public interface UserAccessesService {

    List<UserTaskAccessDto> accessList(Authentication authentication);
}
