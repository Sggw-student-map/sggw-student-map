package com.gwozdz1uuu.sggwstudentmap.auth.jwt;

import com.gwozdz1uuu.sggwstudentmap.user.role.Role;
import lombok.AllArgsConstructor;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;
import java.util.Date;

@AllArgsConstructor
public class Jwt {
    public static final String ROLE_CLAIM = "role";

    private final Claims claims;
    private final SecretKey secretKey;

    public boolean isExpired(){
        return claims.getExpiration().before(new Date());
    }

    public Long getUserId(){
        return Long.valueOf(claims.getSubject());
    }

    // stare tokeny moga nie miec roszczenia "role" -> traktujemy jak USER
    public Role getRole() {
        var raw = claims.get(ROLE_CLAIM, String.class);
        if (raw == null) {
            return Role.USER;
        }
        try {
            return Role.valueOf(raw);
        } catch (IllegalArgumentException ex) {
            return Role.USER;
        }
    }

    public String toString(){
        return Jwts.builder().claims(claims).signWith(secretKey).compact();
    }
}
