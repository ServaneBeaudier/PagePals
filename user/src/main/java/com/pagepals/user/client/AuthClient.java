package com.pagepals.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth", path = "/api/auth")
public interface AuthClient {

    @DeleteMapping("/cleanup/{userId}")
    void cleanupAuthUser(@PathVariable("userId") Long userId);
}
