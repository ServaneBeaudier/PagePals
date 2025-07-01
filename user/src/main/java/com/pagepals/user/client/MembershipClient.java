package com.pagepals.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name= "membership", path = "/api/memberships")
public interface MembershipClient {

    @DeleteMapping("/by-user/{userId}")
    void supprimerInscriptions(@PathVariable("userId") Long userId);

}
