package com.pagepals.search.service;

import java.util.List;

import com.pagepals.search.dto.CircleDTO;
import com.pagepals.search.dto.SearchCriteriaDTO;

public interface SearchService {

    List<CircleDTO> searchCircles(SearchCriteriaDTO criteria);
}
