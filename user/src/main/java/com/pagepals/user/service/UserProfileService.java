package com.pagepals.user.service;

import com.pagepals.user.dto.UserProfileCreateRequest;

public interface UserProfileService {

    void createProfile(UserProfileCreateRequest request);
}
