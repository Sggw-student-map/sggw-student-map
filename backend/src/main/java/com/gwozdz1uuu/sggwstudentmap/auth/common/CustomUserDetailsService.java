package com.gwozdz1uuu.sggwstudentmap.auth.common;

import com.gwozdz1uuu.sggwstudentmap.user.UserRepository;
import com.gwozdz1uuu.sggwstudentmap.user.role.UserRoleService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserRoleService userRoleService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        var enabled = Boolean.TRUE.equals(user.getIsActive());
        var role = userRoleService.getRole(user.getId());

        return User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities("ROLE_" + role.name())
                .disabled(!enabled)
                .build();
    }
}
