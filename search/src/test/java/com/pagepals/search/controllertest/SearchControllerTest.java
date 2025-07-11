package com.pagepals.search.controllertest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pagepals.search.controller.SearchController;
import com.pagepals.search.dto.CircleDTO;
import com.pagepals.search.dto.SearchCriteriaDTO;
import com.pagepals.search.service.SearchService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class SearchControllerTest {

    @InjectMocks
    private SearchController searchController;

    @Mock
    private SearchService searchService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(searchController).build();
    }

    @Test
    void searchCircles_returnsList() throws Exception {
        SearchCriteriaDTO criteria = new SearchCriteriaDTO();
        criteria.setEstOuvert(true);

        CircleDTO dto = CircleDTO.builder()
                .id(1L)
                .nom("Test Circle")
                .dateRencontre(LocalDate.now().plusDays(1))
                .modeRencontre("PRESENTIEL")
                .nbMaxMembres(10)
                .nombreInscrits(5)
                .estOuvert(true)
                .build();

        when(searchService.searchCircles(any(SearchCriteriaDTO.class))).thenReturn(List.of(dto));

        mockMvc.perform(post("/api/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(criteria)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dto.getId()))
                .andExpect(jsonPath("$[0].nom").value(dto.getNom()))
                .andExpect(jsonPath("$[0].modeRencontre").value(dto.getModeRencontre()))
                .andExpect(jsonPath("$[0].estOuvert").value(dto.isEstOuvert()));

        verify(searchService).searchCircles(any(SearchCriteriaDTO.class));
    }
}
