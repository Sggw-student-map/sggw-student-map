package com.gwozdz1uuu.sggwstudentmap.auth.login;

import com.gwozdz1uuu.sggwstudentmap.auth.jwt.Jwt;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginResponse {
    private Jwt accessToken;
    private Jwt refreshToken;
}
