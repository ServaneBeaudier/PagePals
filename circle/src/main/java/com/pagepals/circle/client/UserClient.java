package com.pagepals.circle.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pagepals.circle.dto.MessageDTO;

@FeignClient(name = "user", path = "/api/user")
public interface UserClient {

    @GetMapping("/message")
    List<MessageDTO> getInfosPourMessage(@RequestParam("ids") List<Long> userIds);
}
