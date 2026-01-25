package com.vpe.finalstore.auth.filters;

import com.vpe.finalstore.auth.services.JwtService;
import com.vpe.finalstore.users.enums.RoleEnum;
import com.vpe.finalstore.users.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        var authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        var token = authHeader.replace("Bearer ", "");

        var jwt = jwtService.parseToken(token);

        if (jwt == null || jwt.isExpired()) {
            filterChain.doFilter(request, response);
            return;
        }

        var userId = jwt.getUserId();

        var user = userRepository.findById(userId)
            .orElseThrow(() -> new BadCredentialsException("User not found"));

        var authorities = jwt.getRoles().stream()
                .map(RoleEnum::valueOf)
                .map(RoleEnum::toAuthority)
                .toList();

        if (!jwt.getTokenVersion().equals(user.getTokenVersion())) {
            throw new BadCredentialsException("Token revoked");
        }

        var authentication = new UsernamePasswordAuthenticationToken(
            userId,
            null,
            authorities
        );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
