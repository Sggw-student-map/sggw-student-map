package com.gwozdz1uuu.sggwstudentmap.user;

import com.gwozdz1uuu.sggwstudentmap.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        // Standalone setup — no Spring context, pure unit test.
        // GlobalExceptionHandler is registered so exception-to-status mapping is covered.
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new JacksonJsonHttpMessageConverter())
                .build();
    }

    // ----------------------------------------------------------------
    // Helper
    // ----------------------------------------------------------------

    private UserResponse sampleUser() {
        return new UserResponse(
                1,
                "Jan",
                "Kowalski",
                "jkowalski",
                "jan.kowalski@sggw.edu.pl",
                true,
                LocalDateTime.of(2024, 1, 1, 10, 0),
                LocalDateTime.of(2024, 1, 1, 10, 0)
        );
    }

    // ----------------------------------------------------------------
    // GET /users
    // ----------------------------------------------------------------

    @Test
    void getAllUsers_returnsOkWithList() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(sampleUser()));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Jan"))
                .andExpect(jsonPath("$[0].lastName").value("Kowalski"))
                .andExpect(jsonPath("$[0].username").value("jkowalski"))
                .andExpect(jsonPath("$[0].email").value("jan.kowalski@sggw.edu.pl"))
                .andExpect(jsonPath("$[0].isActive").value(true))
                // password must never appear in the response
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    @Test
    void getAllUsers_returnsEmptyList() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ----------------------------------------------------------------
    // GET /users/{userId}
    // ----------------------------------------------------------------

    @Test
    void getUserById_existingUser_returnsOk() throws Exception {
        when(userService.getUserById(1)).thenReturn(sampleUser());

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Jan"))
                .andExpect(jsonPath("$.lastName").value("Kowalski"))
                .andExpect(jsonPath("$.username").value("jkowalski"))
                .andExpect(jsonPath("$.email").value("jan.kowalski@sggw.edu.pl"))
                .andExpect(jsonPath("$.isActive").value(true))
                // password must never appear in the response
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void getUserById_nonExistingUser_returns404() throws Exception {
        // The controller's responsibility is to propagate UserNotFoundException.
        // GlobalExceptionHandler maps it to 404 — that mapping is what we verify here.
        // The exact error body format belongs in a GlobalExceptionHandler unit test.
        when(userService.getUserById(999)).thenThrow(new UserNotFoundException(999));

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserById_invalidIdType_returns400() throws Exception {
        mockMvc.perform(get("/users/abc"))
                .andExpect(status().isBadRequest());
    }
}
