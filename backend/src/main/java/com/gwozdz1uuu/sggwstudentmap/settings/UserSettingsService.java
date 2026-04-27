package com.gwozdz1uuu.sggwstudentmap.settings;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserSettingsService {

    private final UserSettingsRepository repository;

    public UserSettingsResponse getSettings(Integer userId) {
        var settings = repository.findById(userId).orElseGet(() -> createDefaultSettings(userId));
        return toResponse(settings);
    }

    public UserSettingsResponse updateSettings(Integer userId, UpdateSettingsRequest request) {
        var settings = repository.findById(userId).orElseGet(() -> {
            var s = new UserSettings();
            s.setIdUser(userId);
            return s;
        });

        if (request.privateAccount() != null) {
            settings.setPrivateAccount(request.privateAccount());
        }
        
        settings.setGdprConsent(true);

        return toResponse(repository.save(settings));
    }

    private UserSettings createDefaultSettings(Integer userId) {
        var settings = new UserSettings();
        settings.setIdUser(userId);
        settings.setPrivateAccount(false);
        settings.setGdprConsent(true); 
        return repository.save(settings);
    }

    private UserSettingsResponse toResponse(UserSettings s) {
        return new UserSettingsResponse(
            s.getPrivateAccount(),
            true
        );
    }
}