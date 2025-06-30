package com.pagepals.membership.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pagepals.membership.dto.ParticipantDTO;

@FeignClient(name = "user", path ="/api/user")
public interface UserClient {

    @GetMapping("/pseudos")
    List<ParticipantDTO> getPseudosByIds(@RequestParam("ids") List<Long> userIds);
}
