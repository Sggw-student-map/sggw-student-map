package com.gwozdz1uuu.sggwstudentmap.user.role;

import com.gwozdz1uuu.sggwstudentmap.user.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserRoleService {

    public static final Role DEFAULT_ROLE = Role.USER;

    private final UserRoleRepository userRoleRepository;

    public Role getRole(Integer userId) {
        if (userId == null) {
            return DEFAULT_ROLE;
        }
        return userRoleRepository.findByUser_Id(userId)
                .map(UserRole::getRole)
                .orElse(DEFAULT_ROLE);
    }

    public Role getRoleByUsername(String username) {
        if (username == null) {
            return DEFAULT_ROLE;
        }
        return userRoleRepository.findByUser_Username(username)
                .map(UserRole::getRole)
                .orElse(DEFAULT_ROLE);
    }

    // Idempotentne nadanie domyslnej roli (USER) - bezpieczne do wywolania ponownie
    public UserRole assignDefaultRole(User user) {
        return assignRole(user, DEFAULT_ROLE);
    }

    public UserRole assignRole(User user, Role role) {
        return userRoleRepository.findByUser_Id(user.getId())
                .map(existing -> {
                    existing.setRole(role);
                    return userRoleRepository.save(existing);
                })
                .orElseGet(() -> userRoleRepository.save(new UserRole(user, role)));
    }
}
