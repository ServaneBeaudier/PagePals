package com.pagepals.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pagepals.user.dto.UserInfoDTO;

@FeignClient(name = "user", path ="/api/user")
public interface UserClient {

    @GetMapping("/infos")
    UserInfoDTO getUserInfo(@RequestParam("id") Long userId);
}
