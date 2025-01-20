package ru.spbstu.rakitin.administration.controller.auth;

import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.rakitin.administration.dto.UserPermissionDto;
import ru.spbstu.rakitin.administration.dto.mappers.PermissionMapper;
import ru.spbstu.rakitin.administration.dto.mappers.UserMapper;
import ru.spbstu.rakitin.administration.exceptions.*;
import ru.spbstu.rakitin.administration.model.Permission;
import ru.spbstu.rakitin.administration.model.User;
import ru.spbstu.rakitin.administration.service.AuthenticationService;
import ru.spbstu.rakitin.administration.service.auth.JwtService;
import ru.spbstu.rakitin.administration.service.auth.PermissionService;
import ru.spbstu.rakitin.administration.service.auth.UserService;
import ru.spbstu.rakitin.commonstarter.dto.AuthUserDto;
import ru.spbstu.rakitin.commonstarter.dto.UserDto;
import ru.spbstu.rakitin.commonstarter.dto.ValidateUserTokenDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/user")
public class UserController {

    private final UserService userService;
    private final PermissionMapper permissionMapper;

    private final PermissionService permissionService;

    private final AuthenticationService authenticationService;

    private final JwtService jwtService;

    private final AuthenticationProvider authenticationProvider;

    public UserController(UserService userService, PermissionMapper permissionMapper, PermissionService permissionService, AuthenticationService authenticationService, JwtService jwtService, PasswordEncoder passwordEncoder, AuthenticationProvider authenticationProvider) {
        this.userService = userService;
        this.permissionMapper = permissionMapper;
        this.permissionService = permissionService;
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
        this.authenticationProvider = authenticationProvider;
    }

    @PostMapping("/register")
    public long register(@Valid @RequestBody AuthUserDto userDto) throws UserAlreadyExistsException {
        User user = UserMapper.authUserToUser(userDto);
        return userService.register(user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) throws UserNotFoundException {
        userService.delete(id);
    }

    @PostMapping("/login")
    public String login(@Valid @RequestBody AuthUserDto userDto) {

        Authentication authenticate = authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword()));
        return jwtService.generateToken((UserDetails) authenticate.getPrincipal());
    }


    @PostMapping("/permission")
    public void addPermission(@Valid @RequestBody UserPermissionDto userPermissionDto) throws UserNotFoundException, ProjectNotFoundException, PermissionAlreadyExistsException {
        Permission permission = permissionMapper.fromUserPermissionDtoToPermission(userPermissionDto);
        permissionService.savePermission(permission);
    }

    @GetMapping("/permission/{userId}")
    public List<UserPermissionDto> findAllPermissionsByUser(@PathVariable Long userId) {
        return permissionService.findAllPermissionsForUser(userId).stream().map(permissionMapper::fromPermissionToUserPermissionDto).toList();
    }

    @PostMapping("/validate")
    public UserDto validateUserToken(@RequestBody ValidateUserTokenDto validateUserTokenDto) throws UserNotFoundException {
        String jwt = validateUserTokenDto.getToken();

        String userName = jwtService.extractUserName(jwt);

        if (StringUtils.isNotEmpty(userName)) {
            User userDetails = userService.loadUserByUsername(userName);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                return UserDto.builder()
                        .username(userName)
                        .isValid(true)
                        .id(userDetails.getId())
                        .authorities(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()).
                        build();
            }

        }
        return UserDto.builder().isValid(false).build();
    }

    @GetMapping("/{username}")
    public UserDto getUserByUsername(@PathVariable String username) throws UserNotFoundException {
        User userByUsername = userService.loadUserByUsername(username);
        return UserDto.builder()
                .username(userByUsername.getUsername())
                .password(userByUsername.getPassword())
                .isValid(true)
                .id(userByUsername.getId())
                .authorities(userByUsername.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()).
                build();
    }
}
