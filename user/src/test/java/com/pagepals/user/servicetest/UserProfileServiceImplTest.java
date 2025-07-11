package com.pagepals.user.servicetest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import com.pagepals.user.client.AuthClient;
import com.pagepals.user.client.CircleClient;
import com.pagepals.user.dto.UpdateUserProfileDTO;
import com.pagepals.user.dto.UserProfileCreateRequest;
import com.pagepals.user.model.UserProfile;
import com.pagepals.user.repository.UserProfileRepository;
import com.pagepals.user.service.UserProfileServiceImpl;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.util.Optional;

class UserProfileServiceImplTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private CircleClient circleClient;

    @Mock
    private AuthClient authClient;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createProfile_shouldSaveUserProfileWithCorrectData() {
        // Arrange
        UserProfileCreateRequest request = new UserProfileCreateRequest();
        request.setId(10L);
        request.setPseudo("TestUser");

        ArgumentCaptor<UserProfile> captor = ArgumentCaptor.forClass(UserProfile.class);

        // Act
        userProfileService.createProfile(request);

        // Assert
        verify(userProfileRepository, times(1)).save(captor.capture());
        UserProfile savedProfile = captor.getValue();

        assertEquals(10L, savedProfile.getId());
        assertEquals("TestUser", savedProfile.getPseudo());
        assertEquals(LocalDate.now(), savedProfile.getDateInscription());
    }

    @Test
    void updateProfile_userFoundWithBio_updatesSuccessfully() {
        UpdateUserProfileDTO dto = new UpdateUserProfileDTO();
        dto.setId(1L);
        dto.setPseudo("nouveauPseudo");
        dto.setBio("nouvelle bio");

        UserProfile existing = new UserProfile();
        existing.setId(1L);
        existing.setPseudo("ancienPseudo");
        existing.setBio("ancienne bio");

        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(existing));

        userProfileService.updateProfile(dto);

        assertEquals("nouveauPseudo", existing.getPseudo());
        assertEquals("nouvelle bio", existing.getBio());
        verify(userProfileRepository).save(existing);
    }

    @Test
    void updateProfile_userFoundWithoutBio_updatesSuccessfully() {
        UpdateUserProfileDTO dto = new UpdateUserProfileDTO();
        dto.setId(1L);
        dto.setPseudo("nouveauPseudo");
        dto.setBio(null);

        UserProfile existing = new UserProfile();
        existing.setId(1L);
        existing.setPseudo("ancienPseudo");
        existing.setBio("ancienne bio");

        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(existing));

        userProfileService.updateProfile(dto);

        assertEquals("nouveauPseudo", existing.getPseudo());
        assertEquals("ancienne bio", existing.getBio()); // bio non modifiée
        verify(userProfileRepository).save(existing);
    }

    @Test
    void updateProfile_userNotFound_throwsEntityNotFoundException() {
        UpdateUserProfileDTO dto = new UpdateUserProfileDTO();
        dto.setId(99L);
        dto.setPseudo("pseudo");

        when(userProfileRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userProfileService.updateProfile(dto));
    }

    @Test
    void updatePhoto_userFound_updatesPhotoSuccessfully() {
        long userId = 1L;
        String newFileName = "photo.png";

        UserProfile existing = new UserProfile();
        existing.setId(userId);
        existing.setPhotoProfil("anciennePhoto.png");

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(existing));

        userProfileService.updatePhoto(userId, newFileName);

        assertEquals(newFileName, existing.getPhotoProfil());
        verify(userProfileRepository).save(existing);
    }

    @Test
    void updatePhoto_userNotFound_throwsEntityNotFoundException() {
        long userId = 99L;
        String newFileName = "photo.png";

        when(userProfileRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userProfileService.updatePhoto(userId, newFileName));
    }

    @Test
    void cleanupUser_userFound_callsClientsAndAnonymizes() {
        Long userId = 1L;
        UserProfile user = new UserProfile();
        user.setId(userId);
        user.setPseudo("AncienPseudo");
        user.setPhotoProfil("ancienne.jpg");
        user.setBio("ancienne bio");

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));

        userProfileService.cleanupUser(userId);

        verify(circleClient).deleteActiveCirclesByCreateur(userId);
        verify(circleClient).anonymizeUserInArchivedCircles(userId);
        verify(authClient).cleanupAuthUser(userId);

        assertEquals("Utilisateur supprimé", user.getPseudo());
        assertNull(user.getPhotoProfil());
        assertNull(user.getBio());

        verify(userProfileRepository).save(user);
    }

    @Test
    void cleanupUser_userNotFound_throwsEntityNotFoundException() {
        Long userId = 99L;

        when(userProfileRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userProfileService.cleanupUser(userId));
    }

}
