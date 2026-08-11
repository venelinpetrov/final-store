package com.vpe.finalstore.auth.controllers;

import com.vpe.finalstore.auth.config.CookieConfig;
import com.vpe.finalstore.auth.config.JwtConfig;
import com.vpe.finalstore.auth.dtos.ChangePasswordRequest;
import com.vpe.finalstore.auth.dtos.JwtResponse;
import com.vpe.finalstore.auth.dtos.LoginDto;
import com.vpe.finalstore.auth.services.Jwt;
import com.vpe.finalstore.auth.services.JwtService;
import com.vpe.finalstore.auth.services.PasswordService;
import com.vpe.finalstore.auth.services.RefreshTokenService;
import com.vpe.finalstore.users.dtos.UserDto;
import com.vpe.finalstore.users.mappers.UserMapper;
import com.vpe.finalstore.users.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final PasswordService passwordService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtConfig jwtConfig;
    private final CookieConfig cookieConfig;
    private final RefreshTokenService refreshTokenService;

    private static final String REFRESH_TOKEN_COOKIE_NAME = "finalstore_refreshToken";
    private static final String REFRESH_TOKEN_COOKIE_PATH = "/api/auth";

    @Operation(
        summary = "Login with email and password"
    )
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginDto body, HttpServletResponse response) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(body.getEmail(), body.getPassword())
        );

        var user = userRepository.findByEmail(body.getEmail()).orElseThrow();
        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        var cookie = getCookie(refreshToken);

        response.addCookie(cookie);

        refreshTokenService.saveToken(refreshToken, user);

        return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
        @CookieValue(name = REFRESH_TOKEN_COOKIE_NAME, required = true) String refreshToken,
        HttpServletResponse response
    ) {
        var jwt = jwtService.parseToken(refreshToken);

        refreshTokenService.revokeToken(jwt);

        var cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, "");

        cookie.setPath(REFRESH_TOKEN_COOKIE_PATH);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);

        response.addCookie(cookie);

        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Refresh access token using refresh token"
    )
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(
        @CookieValue(name= REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
        HttpServletResponse response
    ) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var jwt = jwtService.parseToken(refreshToken);

        refreshTokenService.revokeToken(jwt);

        var user = userRepository.findById(jwt.getUserId()).orElseThrow();
        var accessToken = jwtService.generateAccessToken(user);
        var newRefreshToken = jwtService.generateRefreshToken(user);
        var cookie = getCookie(newRefreshToken);

        response.addCookie(cookie);

        refreshTokenService.saveToken(newRefreshToken, user);

        return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }

    @Operation(
        summary = "Get current authenticated user"
    )
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Integer) authentication.getPrincipal();

        var user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        var userDto = userMapper.toDto(user);

        return ResponseEntity.ok(userDto);
    }

    @Operation(
        summary = "Change password for current user"
    )
    @PostMapping("/me/password")
    public ResponseEntity<Void> changePassword(
        @Valid @RequestBody ChangePasswordRequest request,
        Authentication authentication
    ) {
        passwordService.changePassword(authentication, request);

        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private Cookie getCookie(Jwt refreshToken) {
        var cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken.toString());

        cookie.setHttpOnly(true);
        cookie.setPath(REFRESH_TOKEN_COOKIE_PATH);
        cookie.setSecure(cookieConfig.isSecure());
        cookie.setAttribute("SameSite", cookieConfig.getSameSite());
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration());

        return cookie;
    }
}