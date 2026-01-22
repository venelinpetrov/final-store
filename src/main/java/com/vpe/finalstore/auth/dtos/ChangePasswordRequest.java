package com.vpe.finalstore.auth.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
    @NotBlank String currentPassword,
    @NotBlank @Size(min = 6, message = "Password must be at least 6 characters") String newPassword,
    @NotBlank String confirmPassword
) {}
