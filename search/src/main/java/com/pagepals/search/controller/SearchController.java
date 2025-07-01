package com.pagepals.search.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.pagepals.search.dto.CircleDTO;
import com.pagepals.search.dto.SearchCriteriaDTO;
import com.pagepals.search.service.SearchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @PostMapping("/search")
    public List<CircleDTO> searchCircles(@RequestBody SearchCriteriaDTO criteria) {
        return searchService.searchCircles(criteria);
    }
}
