package ru.spbstu.rakitin.administration.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.spbstu.rakitin.administration.exceptions.AdminTokenNotFoundException;
import ru.spbstu.rakitin.administration.service.admin.AdminService;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OnlyAdminSecurityFilterChain extends OncePerRequestFilter {

    private static final String ADMIN_TOKEN_HEADER = "MONITORING-ADMIN-TOKEN";
    private final AdminService adminService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            Optional<String> adminToken = getAdminToken(request);
            if (adminToken.isEmpty()) {
                throw new AdminTokenNotFoundException("Admin token not found in " + ADMIN_TOKEN_HEADER + " header!");
            }
            String adminName = adminService.isTokenValidAndReturnAdmin(adminToken.get());
            log.info("Perform {} request {}", adminName, request.getRequestURL().toString());
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
        filterChain.doFilter(request, response);

    }

    private Optional<String> getAdminToken(HttpServletRequest request) {
        String token = request.getHeader(ADMIN_TOKEN_HEADER);
        if (StringUtils.isEmpty(token)) {
            return Optional.empty();
        }
        return Optional.of(token);
    }

}
