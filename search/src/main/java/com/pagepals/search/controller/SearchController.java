package com.pagepals.search.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pagepals.search.dto.CircleDTO;
import com.pagepals.search.dto.SearchCriteriaDTO;
import com.pagepals.search.service.SearchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @PostMapping
    public ResponseEntity<List<CircleDTO>> searchCircles(@RequestBody SearchCriteriaDTO criteria) {
        System.out.println("Requête reçue dans le controller avec : " + criteria);
        List<CircleDTO> results = searchService.searchCircles(criteria);
        return ResponseEntity.ok(results);
    }
}
