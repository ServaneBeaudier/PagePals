package com.pagepals.auth.client;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.cloud.openfeign.FeignClient;

import com.pagepals.auth.dto.UserProfileCreateRequest;

@FeignClient(name = "user_service", url = "http://user-service:8082/api/user-profiles")
public interface UserProfileClient {

    @PostMapping
    void createUserProfile(@RequestBody UserProfileCreateRequest request);
}
