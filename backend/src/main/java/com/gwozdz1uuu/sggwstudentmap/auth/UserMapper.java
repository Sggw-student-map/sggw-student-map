package com.gwozdz1uuu.sggwstudentmap.auth;

import com.gwozdz1uuu.sggwstudentmap.user.User;
import com.gwozdz1uuu.sggwstudentmap.user.role.UserRoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserMapper {
    private final UserRoleService userRoleService;

    public UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getIsActive(),
                userRoleService.getRole(user.getId())
        );
    }
}
