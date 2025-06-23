package com.pagepals.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pagepals.user.dto.UserProfileCreateRequest;
import com.pagepals.user.service.UserProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserProfileService userProfileService;

    @PostMapping
    public ResponseEntity<Void> createUserProfile(@RequestBody UserProfileCreateRequest request) {
        userProfileService.createProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
