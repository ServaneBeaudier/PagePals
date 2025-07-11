package com.pagepals.auth.servicetest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.pagepals.auth.client.UserProfileClient;
import com.pagepals.auth.dto.AuthResponseDTO;
import com.pagepals.auth.dto.LoginDTO;
import com.pagepals.auth.dto.RegisterDTO;
import com.pagepals.auth.dto.UpdateMailDTO;
import com.pagepals.auth.dto.UpdatePasswordDTO;
import com.pagepals.auth.dto.UserProfileCreateRequest;
import com.pagepals.auth.exception.EmailAlreadyUsedException;
import com.pagepals.auth.exception.InvalidCredentialException;
import com.pagepals.auth.exception.UserNotFoundException;
import com.pagepals.auth.jwt.JWTGenerator;
import com.pagepals.auth.model.Role;
import com.pagepals.auth.model.UserEntity;
import com.pagepals.auth.repository.UserRepository;
import com.pagepals.auth.service.AuthServiceImpl;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserProfileClient userProfileClient;

    @Mock
    private JWTGenerator jwtGenerator;

    @Test
    void register_success() {
        // données d'entrée
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("test@example.com");
        dto.setMotDePasse("password123");
        dto.setPseudo("testuser");

        // mocks
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getMotDePasse())).thenReturn("encodedPassword");
        // pour save, on simule que l'utilisateur aura un id 1L généré
        UserEntity savedUser = new UserEntity();
        savedUser.setId(1L);
        savedUser.setEmail(dto.getEmail());
        savedUser.setMotDePasse("encodedPassword");
        savedUser.setRole(Role.MEMBRE);

        // on simule que save renvoie l'utilisateur avec id
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity userArg = invocation.getArgument(0);
            userArg.setId(1L); // Simuler la génération d'ID dans l'objet original
            return userArg;
        });

        when(jwtGenerator.generateToken(anyLong(), eq(Role.MEMBRE.name()), eq(dto.getEmail())))
                .thenReturn("mocked-jwt-token");

        // appel méthode
        AuthResponseDTO response = authService.register(dto);

        // vérifications
        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        assertEquals(dto.getEmail(), response.getEmail());
        assertEquals(Role.MEMBRE.name(), response.getRole());
        assertEquals(1L, response.getId());

        // vérifier que userProfileClient a bien été appelé
        ArgumentCaptor<UserProfileCreateRequest> captor = ArgumentCaptor.forClass(UserProfileCreateRequest.class);
        verify(userProfileClient).createUserProfile(captor.capture());
        UserProfileCreateRequest req = captor.getValue();
        assertEquals(1L, req.getId());
        assertEquals(dto.getPseudo(), req.getPseudo());
        assertEquals(LocalDate.now(), req.getDateInscription());
    }

    @Test
    void register_emailAlreadyUsed_throwsException() {
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("test@example.com");
        dto.setPseudo("testuser");
        dto.setMotDePasse("password123");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        EmailAlreadyUsedException ex = assertThrows(EmailAlreadyUsedException.class, () -> {
            authService.register(dto);
        });

        assertEquals("Cet email est déjà utilisé !", ex.getMessage());
    }

    @Test
    void register_pseudoNull_throwsException() {
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("test@example.com");
        dto.setPseudo(null);
        dto.setMotDePasse("password123");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(dto);
        });

        assertEquals("Le pseudo est obligatoire !", ex.getMessage());
    }

    @Test
    void register_pseudoBlank_throwsException() {
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("test@example.com");
        dto.setPseudo("  ");
        dto.setMotDePasse("password123");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(dto);
        });

        assertEquals("Le pseudo est obligatoire !", ex.getMessage());
    }

    @Test
    void login_success() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("user@example.com");
        dto.setMotDePasse("correctPassword");

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail(dto.getEmail());
        user.setMotDePasse("encodedPassword");
        user.setRole(Role.MEMBRE);

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.getMotDePasse(), user.getMotDePasse())).thenReturn(true);
        when(jwtGenerator.generateToken(user.getId(), user.getRole().name(), user.getEmail()))
                .thenReturn("mocked-jwt-token");

        AuthResponseDTO response = authService.login(dto);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        assertEquals(dto.getEmail(), response.getEmail());
        assertEquals(Role.MEMBRE.name(), response.getRole());
        assertEquals(1L, response.getId());
    }

    @Test
    void login_userNotFound_throwsException() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("unknown@example.com");
        dto.setMotDePasse("anyPassword");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());

        InvalidCredentialException ex = assertThrows(InvalidCredentialException.class, () -> {
            authService.login(dto);
        });

        assertEquals("Identifiants incorrects", ex.getMessage());
    }

    @Test
    void login_wrongPassword_throwsException() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("user@example.com");
        dto.setMotDePasse("wrongPassword");

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail(dto.getEmail());
        user.setMotDePasse("encodedPassword");
        user.setRole(Role.MEMBRE);

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.getMotDePasse(), user.getMotDePasse())).thenReturn(false);

        InvalidCredentialException ex = assertThrows(InvalidCredentialException.class, () -> {
            authService.login(dto);
        });

        assertEquals("Identifiants incorrects", ex.getMessage());
    }

    @Test
    void getEmailByUserId_found() {
        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setEmail("user@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        String email = authService.getEmailByUserId(userId);

        assertEquals("user@example.com", email);
    }

    @Test
    void getEmailByUserId_notFound() {
        Long userId = 99L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        String email = authService.getEmailByUserId(userId);

        assertNull(email);
    }

    @Test
    void updateMail_success() {
        UpdateMailDTO dto = new UpdateMailDTO();
        dto.setUserId(1L);
        dto.setNewEmail("newemail@example.com");

        UserEntity user = new UserEntity();
        user.setId(dto.getUserId());
        user.setEmail("oldemail@example.com");

        when(userRepository.findById(dto.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        authService.updateMail(dto);

        // Vérifier que l’email a bien été changé
        assertEquals("newemail@example.com", user.getEmail());

        // Vérifier que save a bien été appelé avec le user modifié
        verify(userRepository).save(user);
    }

    @Test
    void updateMail_userNotFound_throwsException() {
        UpdateMailDTO dto = new UpdateMailDTO();
        dto.setUserId(99L);
        dto.setNewEmail("newemail@example.com");

        when(userRepository.findById(dto.getUserId())).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> {
            authService.updateMail(dto);
        });

        assertEquals("Utilisateur introuvable", ex.getMessage());
    }

    @Test
    void updatePassword_success() {
        UpdatePasswordDTO dto = new UpdatePasswordDTO();
        dto.setUserId(1L);
        dto.setNewMotDePasse("newPassword");

        UserEntity user = new UserEntity();
        user.setId(dto.getUserId());
        user.setMotDePasse("oldEncodedPassword");

        when(userRepository.findById(dto.getUserId())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(dto.getNewMotDePasse())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        authService.updatePassword(dto);

        // Vérifier que le mot de passe a bien été encodé et mis à jour
        assertEquals("newEncodedPassword", user.getMotDePasse());

        // Vérifier que save a bien été appelé
        verify(userRepository).save(user);
    }

    @Test
    void updatePassword_userNotFound_throwsException() {
        UpdatePasswordDTO dto = new UpdatePasswordDTO();
        dto.setUserId(99L);
        dto.setNewMotDePasse("newPassword");

        when(userRepository.findById(dto.getUserId())).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> {
            authService.updatePassword(dto);
        });

        assertEquals("Utilisateur introuvable", ex.getMessage());
    }

    @Test
    void deleteUserById_success() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);

        authService.deleteUserById(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUserById_userNotFound_throwsException() {
        Long userId = 99L;

        when(userRepository.existsById(userId)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            authService.deleteUserById(userId);
        });

        assertEquals("Utilisateur introuvable avec l'id : " + userId, ex.getMessage());
    }

}
