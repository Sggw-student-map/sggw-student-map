package com.gwozdz1uuu.sggwstudentmap.exception;

import com.gwozdz1uuu.sggwstudentmap.user.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleUserNotFound_returns404WithBody() {
        UserNotFoundException ex = new UserNotFoundException(42);

        ResponseEntity<Map<String, Object>> response = handler.handleUserNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsEntry("status", 404);
        assertThat(response.getBody()).containsEntry("error", "Not Found");
        assertThat(response.getBody()).containsEntry("message", "User not found with id: 42");
        assertThat(response.getBody()).containsKey("timestamp");
    }

    @Test
    void handleGeneric_returns500WithBody() {
        Exception ex = new RuntimeException("unexpected failure");

        ResponseEntity<Map<String, Object>> response = handler.handleGeneric(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsEntry("status", 500);
        assertThat(response.getBody()).containsEntry("error", "Internal Server Error");
        assertThat(response.getBody()).containsEntry("message", "unexpected failure");
        assertThat(response.getBody()).containsKey("timestamp");
    }
}
