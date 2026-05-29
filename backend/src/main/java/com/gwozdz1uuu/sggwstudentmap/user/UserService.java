package com.gwozdz1uuu.sggwstudentmap.user;

import com.gwozdz1uuu.sggwstudentmap.settings.UserSettings;
import com.gwozdz1uuu.sggwstudentmap.settings.UserSettingsRepository;
import com.gwozdz1uuu.sggwstudentmap.user.role.UserRoleService;
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
    private final UserRoleService userRoleService;

    // repozytorium i serwis mailowy
    private final com.gwozdz1uuu.sggwstudentmap.repository.UserVerificationTokenRepository tokenRepository;
    private final com.gwozdz1uuu.sggwstudentmap.mail.EmailService emailService;

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
            throw new UserAlreadyExistsException("email", "Ten adres e-mail jest już zarejestrowany.");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException("username", "Ten login jest już zajęty. Wybierz inny.");
        }

        var user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setIsActive(false);

        var saved = userRepository.save(user);

        // kazdy nowy uzytkownik dostaje role USER; ADMIN/APPROVER nadawane recznie w DB
        userRoleService.assignDefaultRole(saved);

        UserSettings settings = new UserSettings();
        settings.setIdUser(saved.getId());
        userSettingsRepository.save(settings);

        String token = java.util.UUID.randomUUID().toString();
        UserVerificationToken verificationToken = new UserVerificationToken(token, saved);
        tokenRepository.save(verificationToken);
        emailService.sendVerificationEmail(saved.getEmail(), token);

        return UserResponse.from(saved);
    }
}
