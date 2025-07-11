package com.pagepals.user.controllertest;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

import com.pagepals.user.controller.UserController;
import com.pagepals.user.dto.*;
import com.pagepals.user.exception.GlobalExceptionHandler;
import com.pagepals.user.exception.UserNotFoundException;
import com.pagepals.user.model.UserProfile;
import com.pagepals.user.repository.UserProfileRepository;
import com.pagepals.user.service.*;

import jakarta.persistence.EntityNotFoundException;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private UserProfileRepository userProfileRepository;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createUserProfile_shouldReturn201() throws Exception {
        UserProfileCreateRequest request = new UserProfileCreateRequest();
        request.setId(1L);
        request.setPseudo("testUser");

        mockMvc.perform(post("/api/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(userProfileService).createProfile(any(UserProfileCreateRequest.class));
    }

    @Test
    void createUserProfile_whenException_shouldReturn500() throws Exception {
        doThrow(new RuntimeException("fail")).when(userProfileService).createProfile(any());

        UserProfileCreateRequest request = new UserProfileCreateRequest();
        request.setId(1L);
        request.setPseudo("testUser");

        mockMvc.perform(post("/api/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        verify(userProfileService).createProfile(any());
    }

    @Test
    void updateUserProfile_shouldReturn201() throws Exception {
        UpdateUserProfileDTO dto = new UpdateUserProfileDTO();
        dto.setId(1L);
        dto.setPseudo("updatedUser");

        mockMvc.perform(put("/api/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(userProfileService).updateProfile(any(UpdateUserProfileDTO.class));
    }

    @Test
    void uploadPhoto_shouldReturn200_whenFileNotEmpty() throws Exception {
        byte[] content = "some image content".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", MediaType.IMAGE_JPEG_VALUE, content);

        when(fileStorageService.storeFile(any())).thenReturn("photo.jpg");

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/user/update/photo")
                .file(file)
                .param("userId", "1")
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isOk());

        verify(userProfileService).updatePhoto(eq(1L), eq("photo.jpg"));
    }

    @Test
    void uploadPhoto_shouldReturn400_whenFileEmpty() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "", MediaType.IMAGE_JPEG_VALUE, new byte[0]);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/user/update/photo")
                .file(file)
                .param("userId", "1")
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isBadRequest());

        verify(fileStorageService, never()).storeFile(any());
        verify(userProfileService, never()).updatePhoto(anyLong(), any());
    }

    @Test
    void deletePhoto_shouldReturn200_whenPhotoExists() throws Exception {
        UserProfile user = new UserProfile();
        user.setId(1L);
        user.setPhotoProfil("photo.jpg");

        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(delete("/api/user/photo")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Photo de profil supprimée."));

        verify(fileStorageService).deleteFile("photo.jpg");
        verify(userProfileRepository).save(user);
    }

    @Test
    void deletePhoto_shouldReturn400_whenNoPhoto() throws Exception {
        UserProfile user = new UserProfile();
        user.setId(1L);
        user.setPhotoProfil(null);

        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(delete("/api/user/photo")
                .param("userId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Aucune photo à supprimer."));

        verify(fileStorageService, never()).deleteFile(any());
        verify(userProfileRepository, never()).save(any());
    }

    @Test
    void deletePhoto_shouldReturn404_whenUserNotFound() throws Exception {
        when(userProfileRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/user/photo")
                .param("userId", "1"))
                .andExpect(status().isNotFound());

        verify(fileStorageService, never()).deleteFile(any());
    }

    @Test
    void getPhoto_shouldReturnFile() throws Exception {
        byte[] content = "file content".getBytes();
        Resource resource = new ByteArrayResource(content);

        when(fileStorageService.loadFile(anyString())).thenReturn(resource);

        mockMvc.perform(get("/api/user/photo/photo.jpg"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/octet-stream"));
    }

    @Test
    void getPhoto_shouldReturn404_whenFileNotFound() throws Exception {
        when(fileStorageService.loadFile("photo.jpg")).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/api/user/photo/photo.jpg"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPseudosByIds_shouldReturnList() throws Exception {
        UserProfile user1 = new UserProfile();
        user1.setId(1L);
        user1.setPseudo("user1");
        user1.setPhotoProfil("photo1.jpg");

        UserProfile user2 = new UserProfile();
        user2.setId(2L);
        user2.setPseudo("user2");
        user2.setPhotoProfil(null);

        when(userProfileRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/api/user/pseudos")
                .param("ids", "1", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].pseudo").value("user1"))
                .andExpect(jsonPath("$[0].photoProfil").value("photo1.jpg"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].pseudo").value("user2"))
                .andExpect(jsonPath("$[1].photoProfil").isEmpty());
    }

    @Test
    void getUserInfo_shouldReturnUserInfo() throws Exception {
        UserProfile user = new UserProfile();
        user.setId(1L);
        user.setPseudo("user1");
        user.setPhotoProfil("photo1.jpg");
        user.setDateInscription(LocalDate.of(2023, 1, 1));
        user.setBio("bio test");

        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/user/infos")
                .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.pseudo").value("user1"))
                .andExpect(jsonPath("$.photoProfil").value("photo1.jpg"))
                .andExpect(jsonPath("$.dateInscription").value("2023-01-01"))
                .andExpect(jsonPath("$.bio").value("bio test"));
    }

    @Test
    void getUserInfo_shouldReturn404_whenNotFound() throws Exception {
        when(userProfileRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/user/infos")
                .param("id", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getInfosPourMessage_shouldReturnList() throws Exception {
        UserProfile user = new UserProfile();
        user.setId(1L);
        user.setPseudo("user1");
        user.setPhotoProfil("photo1.jpg");

        when(userProfileRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(user));

        mockMvc.perform(get("/api/user/message")
                .param("ids", "1", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].auteurId").value(1))
                .andExpect(jsonPath("$[0].pseudoAuteur").value("user1"))
                .andExpect(jsonPath("$[0].photoAuteur").value("photo1.jpg"));
    }

    @Test
    void cleanupCompte_shouldReturn204() throws Exception {
        doNothing().when(userProfileService).cleanupUser(1L);

        mockMvc.perform(delete("/api/user/cleanup")
                .param("userId", "1"))
                .andExpect(status().isNoContent());

        verify(userProfileService).cleanupUser(1L);
    }

    @Test
    void cleanupCompte_shouldReturn500_whenOtherException() throws Exception {
        doThrow(new RuntimeException("Erreur serveur")).when(userProfileService).cleanupUser(1L);

        mockMvc.perform(delete("/api/user/cleanup")
                .param("userId", "1"))
                .andExpect(status().isInternalServerError());

        verify(userProfileService).cleanupUser(1L);
    }

}
