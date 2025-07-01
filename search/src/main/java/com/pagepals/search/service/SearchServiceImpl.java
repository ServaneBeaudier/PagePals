package com.pagepals.search.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pagepals.search.client.CircleClient;
import com.pagepals.search.dto.CircleDTO;
import com.pagepals.search.dto.SearchCriteriaDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService{

    private final CircleClient circleClient;

    @Override
    public List<CircleDTO> searchCircles(SearchCriteriaDTO criteria) {
        return circleClient.searchCircles(criteria);
    }

}
