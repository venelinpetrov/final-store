package com.vpe.finalstore.auth.services;

import com.vpe.finalstore.auth.dtos.ChangePasswordRequest;
import com.vpe.finalstore.exceptions.BadRequestException;
import com.vpe.finalstore.users.exceptions.UserNotFoundException;
import com.vpe.finalstore.users.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class PasswordService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void changePassword(Authentication authentication, ChangePasswordRequest request) {
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        Integer userId = Integer.valueOf(authentication.getName());

        var user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
    }
}
