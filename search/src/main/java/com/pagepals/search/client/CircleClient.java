package com.pagepals.search.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.pagepals.search.dto.CircleDTO;
import com.pagepals.search.dto.SearchCriteriaDTO;

@FeignClient(name = "circle", path = "/api/circles")
public interface CircleClient {

    @PostMapping("/search")
    List<CircleDTO> searchCircles(@RequestBody SearchCriteriaDTO criteria);
}
