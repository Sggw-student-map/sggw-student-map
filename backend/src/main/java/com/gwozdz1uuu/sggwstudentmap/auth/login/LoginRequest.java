package com.gwozdz1uuu.sggwstudentmap.auth.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Username is required")
    @Size(max = 100, message = "Username is too long")
    private String username;

    @NotBlank(message = "Password is required.")
    @Size(min = 6,  max = 25, message = "Password must be between 6 to 25 character long")
    private String password;
}
