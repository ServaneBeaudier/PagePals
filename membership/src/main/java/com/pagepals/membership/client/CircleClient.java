package com.pagepals.membership.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.pagepals.membership.dto.CircleDTO;


@FeignClient(name = "circle", path = "/api/circles")
public interface CircleClient {

    @GetMapping("/{id}")
    CircleDTO getCircleById(@PathVariable("id") long id);
}
