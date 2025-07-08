package com.pagepals.user.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pagepals.user.client.AuthClient;
import com.pagepals.user.client.CircleClient;
import com.pagepals.user.dto.UpdateUserProfileDTO;
import com.pagepals.user.dto.UserProfileCreateRequest;
import com.pagepals.user.model.UserProfile;
import com.pagepals.user.repository.UserProfileRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;

    private final CircleClient circleClient;

    private final AuthClient authClient;

    @Override
    public void createProfile(UserProfileCreateRequest request) {
        UserProfile profile = new UserProfile();
        profile.setId(request.getId());
        profile.setPseudo(request.getPseudo());
        profile.setDateInscription(LocalDate.now());

        userProfileRepository.save(profile);
    }

    @Override
    public void updateProfile(UpdateUserProfileDTO dto) {
        UserProfile userExisting = userProfileRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
        System.out.println("ID reçu pour mise à jour : " + dto.getId());

        userExisting.setPseudo(dto.getPseudo());

        if (dto.getBio() != null) {
            userExisting.setBio(dto.getBio());
        }

        userProfileRepository.save(userExisting);
    }

    @Override
    public void updatePhoto(long userId, String fileName) {
        UserProfile userExisting = userProfileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        userExisting.setPhotoProfil(fileName);
        userProfileRepository.save(userExisting);
    }

    @Override
    @Transactional
    public void cleanupUser(Long userId) {
        UserProfile user = userProfileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        // Appels microservices circle
        circleClient.deleteActiveCirclesByCreateur(userId);
        circleClient.anonymizeUserInArchivedCircles(userId);
        authClient.cleanupAuthUser(userId);

        // Anonymisation locale utilisateur
        user.setPseudo("Utilisateur supprimé");
        user.setPhotoProfil(null);
        user.setBio(null);

        userProfileRepository.save(user);
    }

}
