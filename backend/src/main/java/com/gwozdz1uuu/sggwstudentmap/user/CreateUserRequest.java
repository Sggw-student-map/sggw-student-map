package com.gwozdz1uuu.sggwstudentmap.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name is too long")
        String firstName,
        @NotBlank(message = "Last name is required")
        @Size(max = 100, message = "Last name is too long")
        String lastName,
        @NotBlank(message = "Username is required")
        @Size(max = 100, message = "Username is too long")
        String username,
        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 25, message = "Password must be between 6 and 25 characters")
        String password,
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 255, message = "Email is too long")
        String email
) {
}
