package com.pagepals.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth", path = "/api/auth")
public interface AuthClient {

    @PutMapping("/cleanup")
    void cleanupAuthUser(@RequestParam("userId") Long userId);
}
