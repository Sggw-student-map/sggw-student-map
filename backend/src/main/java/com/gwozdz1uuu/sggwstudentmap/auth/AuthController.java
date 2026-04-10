package com.gwozdz1uuu.sggwstudentmap.auth;


import com.gwozdz1uuu.sggwstudentmap.auth.jwt.JwtConfig;
import com.gwozdz1uuu.sggwstudentmap.auth.jwt.JwtResponse;
import com.gwozdz1uuu.sggwstudentmap.auth.login.LoginRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {
    private final JwtConfig jwtConfig;
    private final UserMapper userMapper;
    private final AuthService authService;
    @Value("${app.auth.refresh-cookie-secure:true}")
    private boolean refreshCookieSecure;
    @Value("${app.auth.refresh-cookie-same-site:Lax}")
    private String refreshCookieSameSite;

    @PostMapping("/login")
    public JwtResponse login(
            @RequestBody LoginRequest request,
            HttpServletResponse response)
    {
        var loginResult = authService.login(request);

        var refreshToken = loginResult.getRefreshToken().toString();
        var cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .path("/auth/refresh")
                .maxAge(jwtConfig.getRefreshTokenExpiration())
                .secure(refreshCookieSecure)
                .sameSite(refreshCookieSameSite)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

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
