package com.pagepals.auth.controllertest;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.pagepals.auth.controller.AuthController;
import com.pagepals.auth.dto.*;
import com.pagepals.auth.exception.*;
import com.pagepals.auth.model.Role;
import com.pagepals.auth.service.AuthService;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    void getEmail_found() throws Exception {
        Long userId = 1L;
        String email = "user@example.com";

        when(authService.getEmailByUserId(eq(userId))).thenReturn(email);

        mockMvc.perform(get("/api/auth/email")
                .param("id", userId.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void getEmail_notFound() throws Exception {
        Long userId = 99L;

        when(authService.getEmailByUserId(userId)).thenReturn(null);

        mockMvc.perform(get("/email")
                .param("id", userId.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void register_success() throws Exception {
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("test@example.com");
        dto.setPseudo("testuser");
        dto.setMotDePasse("password123");

        AuthResponseDTO responseDTO = new AuthResponseDTO(
                "mocked-access-token",
                "mocked-refresh-token",
                dto.getEmail(),
                Role.MEMBRE.name(),
                1L);

        when(authService.register(any(RegisterDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "email": "test@example.com",
                                "pseudo": "testuser",
                                "motDePasse": "password123"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("mocked-refresh-token"))
                .andExpect(jsonPath("$.email").value(dto.getEmail()))
                .andExpect(jsonPath("$.role").value(Role.MEMBRE.name()))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void login_success() throws Exception {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("user@example.com");
        dto.setMotDePasse("password123");

        AuthResponseDTO responseDTO = new AuthResponseDTO(
                "mocked-access-token",
                "mocked-refresh-token",
                dto.getEmail(),
                Role.MEMBRE.name(),
                1L);

        when(authService.login(any(LoginDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "email": "user@example.com",
                                "motDePasse": "password123"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"))
                .andExpect(jsonPath("$.email").value(dto.getEmail()))
                .andExpect(jsonPath("$.role").value(Role.MEMBRE.name()))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void login_invalidCredentials_returnsUnauthorized() throws Exception {
        when(authService.login(any(LoginDTO.class)))
                .thenThrow(new InvalidCredentialException("Identifiants incorrects"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "email": "user@example.com",
                                "motDePasse": "wrongPassword"
                            }
                        """))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Identifiants incorrects"));
    }

    @Test
    void updateEmail_success() throws Exception {
        UpdateMailDTO dto = new UpdateMailDTO();
        dto.setUserId(1L);
        dto.setNewEmail("newemail@example.com");

        // Pas besoin de mocker la méthode void qui ne renvoie rien
        doNothing().when(authService).updateMail(any(UpdateMailDTO.class));

        mockMvc.perform(put("/api/auth/update-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "userId": 1,
                                "newEmail": "newemail@example.com"
                            }
                        """))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateEmail_userNotFound_returnsNotFound() throws Exception {
        UpdateMailDTO dto = new UpdateMailDTO();
        dto.setUserId(99L);
        dto.setNewEmail("newemail@example.com");

        doThrow(new UserNotFoundException("Utilisateur introuvable"))
                .when(authService).updateMail(any(UpdateMailDTO.class));

        mockMvc.perform(put("/api/auth/update-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "userId": 99,
                                "newEmail": "newemail@example.com"
                            }
                        """))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Utilisateur introuvable"));
    }

    @Test
    void updatePassword_success() throws Exception {
        UpdatePasswordDTO dto = new UpdatePasswordDTO();
        dto.setUserId(1L);
        dto.setNewMotDePasse("newPassword");

        doNothing().when(authService).updatePassword(any(UpdatePasswordDTO.class));

        mockMvc.perform(put("/api/auth/update-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "userId": 1,
                                "newMotDePasse": "newPassword"
                            }
                        """))
                .andExpect(status().isNoContent());
    }

    @Test
    void updatePassword_userNotFound_returnsNotFound() throws Exception {
        UpdatePasswordDTO dto = new UpdatePasswordDTO();
        dto.setUserId(99L);
        dto.setNewMotDePasse("newPassword");

        doThrow(new UserNotFoundException("Utilisateur introuvable"))
                .when(authService).updatePassword(any(UpdatePasswordDTO.class));

        mockMvc.perform(put("/api/auth/update-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "userId": 99,
                                "newMotDePasse": "newPassword"
                            }
                        """))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Utilisateur introuvable"));
    }

    @Test
    void cleanupAuthUser_success() throws Exception {
        Long userId = 1L;

        doNothing().when(authService).deleteUserById(userId);

        mockMvc.perform(delete("/api/auth/cleanup/{userId}", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    void cleanupAuthUser_userNotFound_returnsNotFound() throws Exception {
        Long userId = 99L;

        doThrow(new UserNotFoundException("Utilisateur introuvable avec l'id : " + userId))
                .when(authService).deleteUserById(userId);

        mockMvc.perform(delete("/api/auth/cleanup/{userId}", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Utilisateur introuvable avec l'id : " + userId));
    }

}
