package com.pagepals.search.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "membership", path = "/api/memberships")
public interface MembershipClient {
    
    @GetMapping("/count/{circleId}")
    int getNombreInscrits(@PathVariable("circleId") long circleId);
}
