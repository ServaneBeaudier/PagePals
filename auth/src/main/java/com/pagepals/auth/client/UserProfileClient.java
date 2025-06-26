package com.pagepals.auth.client;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.cloud.openfeign.FeignClient;

import com.pagepals.auth.dto.UserProfileCreateRequest;

@FeignClient(name = "user")
public interface UserProfileClient {

    @PostMapping("/api/user/create")
    void createUserProfile(@RequestBody UserProfileCreateRequest request);
}
