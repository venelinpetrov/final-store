package com.vpe.finalstore.users.dtos;

import com.vpe.finalstore.common.validation.Lowercase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCreateDto {
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be less than 256 characters")
    private String name;

    @NotBlank( message = "Email is required")
    @Email(message = "Email must be valid")
    @Lowercase(message = "Email must be in lowercase")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 25, message = "Password must be between 6 and 25 characters long")
    private String password;
}
