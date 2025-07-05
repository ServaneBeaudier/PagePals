package com.pagepals.circle.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "membership", path = "/api/memberships")
public interface MembershipClient {

    @PostMapping("/auto-inscription")
    void ajouterCreateurCommeMembre(@RequestParam("circleId") long circleId, @RequestParam("userId") long userId);

    @GetMapping("/count/{circleId}")
    int countMembersForCircle(@PathVariable("circleId") Long circleId);
}
