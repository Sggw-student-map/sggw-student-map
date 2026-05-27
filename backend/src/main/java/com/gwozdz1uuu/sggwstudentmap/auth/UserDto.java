package com.gwozdz1uuu.sggwstudentmap.auth;

import com.gwozdz1uuu.sggwstudentmap.user.role.Role;

public record UserDto(
        Integer id,
        String username,
        String email,
        String firstName,
        String lastName,
        Boolean isActive,
        Role role
) {
}
