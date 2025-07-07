package com.pagepals.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "circle", path = "/api/circles")
public interface CircleClient {

    @DeleteMapping("/active-by-createur/{userId}")
    void deleteActiveCirclesByCreateur(@PathVariable("userId") Long userId);

    @PutMapping("/anonymize-user/{userId}")
    void anonymizeUserInArchivedCircles(@PathVariable("userId") Long userId);
}
