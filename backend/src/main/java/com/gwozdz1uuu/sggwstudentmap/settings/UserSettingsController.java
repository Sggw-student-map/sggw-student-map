package com.gwozdz1uuu.sggwstudentmap.settings;

import com.gwozdz1uuu.sggwstudentmap.auth.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/settings")
@AllArgsConstructor
@Tag(name = "Ustawienia", description = "Ustawienia konta zalogowanego użytkownika")
public class UserSettingsController {

    private final UserSettingsService service;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<UserSettingsResponse> getSettings() {
        return ResponseEntity.ok(service.getSettings(authService.getCurrentUser().getId()));
    }

    @PutMapping
    public ResponseEntity<UserSettingsResponse> updateSettings(@RequestBody UpdateSettingsRequest request) {
        return ResponseEntity.ok(service.updateSettings(authService.getCurrentUser().getId(), request));
    }
}