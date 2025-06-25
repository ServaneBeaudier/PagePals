package com.pagepals.user.service;

import com.pagepals.user.dto.UpdateUserProfileDTO;
import com.pagepals.user.dto.UserProfileCreateRequest;

public interface UserProfileService {

    void createProfile(UserProfileCreateRequest request);
    void updateProfile(UpdateUserProfileDTO dto);
    void updatePhoto(long userId, String fileName);
}
