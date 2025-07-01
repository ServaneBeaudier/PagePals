package com.pagepals.user.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.pagepals.user.dto.CircleDTO;

@FeignClient(name= "circle", path = "/api/circles")
public interface CircleClient {

    @GetMapping("/cleanup/{userId}")
    List<CircleDTO> cleanupUserData(@PathVariable("userId") Long userId);
}
