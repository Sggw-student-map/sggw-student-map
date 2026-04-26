package com.gwozdz1uuu.sggwstudentmap.user;

import com.gwozdz1uuu.sggwstudentmap.settings.UserSettings;
import com.gwozdz1uuu.sggwstudentmap.settings.UserSettingsRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserSettingsRepository userSettingsRepository;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    public UserResponse getUserById(Integer userId) {
        return userRepository.findById(userId)
                .map(UserResponse::from)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("User with this email already exists");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException("User with this username already exists");
        }

        var user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setIsActive(true);

        var saved = userRepository.save(user);

        UserSettings settings = new UserSettings();
        settings.setIdUser(saved.getId());
        userSettingsRepository.save(settings);

        return UserResponse.from(saved);
    }
}
