package com.gwozdz1uuu.sggwstudentmap.auth;

public record UserDto(
        Integer id,
        String username,
        String email,
        String firstName,
        String lastName
) {
}
