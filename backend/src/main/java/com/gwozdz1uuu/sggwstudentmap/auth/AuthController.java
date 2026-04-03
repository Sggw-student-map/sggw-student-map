package com.gwozdz1uuu.sggwstudentmap.auth;


import com.gwozdz1uuu.sggwstudentmap.auth.jwt.JwtConfig;
import com.gwozdz1uuu.sggwstudentmap.auth.jwt.JwtResponse;
import com.gwozdz1uuu.sggwstudentmap.auth.login.LoginRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {
    private final JwtConfig jwtConfig;
    private final UserMapper userMapper;
    private final AuthService authService;

    @PostMapping("/login")
    public JwtResponse login(
            @RequestBody LoginRequest request,
            HttpServletResponse response)
    {
        var loginResult = authService.login(request);

        var refreshToken = loginResult.getRefreshToken().toString();
        var cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration()); //7 dni
        cookie.setSecure(true);
        response.addCookie(cookie);

        var jwtResponse = new JwtResponse();
        jwtResponse.setToken(loginResult.getAccessToken().toString());
        return jwtResponse;
    }

    @PostMapping("/refresh")
    public JwtResponse refresh(
            @CookieValue(value = "refreshToken") String refreshToken){
        var accessToken = authService.refreshAccessToken(refreshToken);
        var jwtResponse = new JwtResponse();
        jwtResponse.setToken(accessToken.toString());
        return jwtResponse;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me() {
        var user = authService.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var userDto = userMapper.toDto(user);
        return ResponseEntity.ok(userDto);
    }

}
