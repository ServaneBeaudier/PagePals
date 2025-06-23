package com.pagepals.user.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.pagepals.user.dto.UserProfileCreateRequest;
import com.pagepals.user.model.UserProfile;
import com.pagepals.user.repository.UserProfileRepository;

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

}
