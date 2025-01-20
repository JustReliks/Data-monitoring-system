package ru.spbstu.rakitin.commonstarter.admin.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.spbstu.rakitin.commonstarter.admin.AdminRequestFactory;
import ru.spbstu.rakitin.commonstarter.dto.UserDto;

//@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AdminRequestFactory adminRequestFactory;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDto userDto = adminRequestFactory.doGet("/api/v1/admin/user/" + username, UserDto.class);
        return SecurityUserDetails.builder()
                .id(userDto.getId())
                .permissions(userDto.getAuthorities())
                .username(userDto.getUsername())
                .password(userDto.getPassword()).build();
    }
}
