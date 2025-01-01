package ru.spbstu.rakitin.administration.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.rakitin.administration.dto.AdminRequestDto;
import ru.spbstu.rakitin.administration.dto.CreateAdminResponseDto;
import ru.spbstu.rakitin.administration.dto.UpdateAdminTokenRequestDto;
import ru.spbstu.rakitin.administration.exceptions.AdminAlreadyExistsException;
import ru.spbstu.rakitin.administration.exceptions.AdminNotFoundException;
import ru.spbstu.rakitin.administration.service.admin.AdminService;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateAdminResponseDto createAdmin(@Valid @RequestBody AdminRequestDto adminRequestDto) throws AdminAlreadyExistsException {
        CreateAdminResponseDto response = new CreateAdminResponseDto();

        String token = adminService.createAdmin(adminRequestDto.getUsername());
        response.setToken(token);

        return response;
    }

    @DeleteMapping("/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAdmin(@Valid @RequestBody AdminRequestDto adminRequestDto) throws AdminNotFoundException {
        adminService.deleteAdmin(adminRequestDto.getUsername());
    }

    @PutMapping("/")
    public CreateAdminResponseDto updateToken(@Valid @RequestBody UpdateAdminTokenRequestDto updateAdminTokenRequestDto) throws AdminNotFoundException {
        CreateAdminResponseDto response = new CreateAdminResponseDto();
        response.setToken(adminService.updateToken(updateAdminTokenRequestDto.getUsername(), updateAdminTokenRequestDto.getOldToken()));

        return response;

    }


}
