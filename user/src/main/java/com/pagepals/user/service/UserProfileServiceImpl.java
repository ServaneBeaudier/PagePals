package com.pagepals.user.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

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

}
