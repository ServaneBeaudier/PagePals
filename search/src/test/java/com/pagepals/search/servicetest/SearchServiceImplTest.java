package com.pagepals.search.servicetest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import com.pagepals.search.client.CircleClient;
import com.pagepals.search.client.MembershipClient;
import com.pagepals.search.dto.CircleDTO;
import com.pagepals.search.dto.SearchCriteriaDTO;
import com.pagepals.search.service.SearchServiceImpl;

class SearchServiceImplTest {

    @Mock
    private CircleClient circleClient;

    @Mock
    private MembershipClient membershipClient;

    @InjectMocks
    private SearchServiceImpl searchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void searchCircles_shouldCallCircleClientAndMembershipClient() {
        SearchCriteriaDTO criteria = new SearchCriteriaDTO();

        CircleDTO c1 = CircleDTO.builder().id(1L).nbMaxMembres(10).build();
        when(circleClient.searchCircles(criteria)).thenReturn(List.of(c1));
        when(membershipClient.getNombreInscrits(1L)).thenReturn(3);

        List<CircleDTO> result = searchService.searchCircles(criteria);

        verify(circleClient).searchCircles(criteria);
        verify(membershipClient).getNombreInscrits(1L);

        assertEquals(1, result.size());
        assertEquals(3, result.get(0).getNombreInscrits());
    }

    @Test
    void searchCircles_shouldSetEstOuvertCorrectly() {
        SearchCriteriaDTO criteria = new SearchCriteriaDTO();

        CircleDTO c1 = CircleDTO.builder().id(1L).nbMaxMembres(5).build();
        CircleDTO c2 = CircleDTO.builder().id(2L).nbMaxMembres(3).build();

        when(circleClient.searchCircles(criteria)).thenReturn(List.of(c1, c2));
        when(membershipClient.getNombreInscrits(1L)).thenReturn(4); // ouvert (4<5)
        when(membershipClient.getNombreInscrits(2L)).thenReturn(3); // fermé (3==3)

        List<CircleDTO> result = searchService.searchCircles(criteria);

        assertTrue(result.stream().filter(c -> c.getId() == 1L).findFirst().get().isEstOuvert());
        assertFalse(result.stream().filter(c -> c.getId() == 2L).findFirst().get().isEstOuvert());
    }

    @Test
    void searchCircles_shouldFilterByEstOuvertWhenSetToTrue() {
        SearchCriteriaDTO criteria = new SearchCriteriaDTO();
        criteria.setEstOuvert(true);

        CircleDTO c1 = CircleDTO.builder().id(1L).nbMaxMembres(5).build();
        CircleDTO c2 = CircleDTO.builder().id(2L).nbMaxMembres(3).build();

        when(circleClient.searchCircles(any())).thenReturn(List.of(c1, c2));
        when(membershipClient.getNombreInscrits(1L)).thenReturn(4); // ouvert (4<5)
        when(membershipClient.getNombreInscrits(2L)).thenReturn(3); // fermé (3==3)

        List<CircleDTO> result = searchService.searchCircles(criteria);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertTrue(result.get(0).isEstOuvert());
    }

    @Test
    void searchCircles_shouldFilterByEstOuvertWhenSetToFalse() {
        SearchCriteriaDTO criteria = new SearchCriteriaDTO();
        criteria.setEstOuvert(false);

        CircleDTO c1 = CircleDTO.builder().id(1L).nbMaxMembres(5).build();
        CircleDTO c2 = CircleDTO.builder().id(2L).nbMaxMembres(3).build();

        when(circleClient.searchCircles(any())).thenReturn(List.of(c1, c2));
        when(membershipClient.getNombreInscrits(1L)).thenReturn(4); // ouvert (4<5)
        when(membershipClient.getNombreInscrits(2L)).thenReturn(3); // fermé (3==3)

        List<CircleDTO> result = searchService.searchCircles(criteria);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
        assertFalse(result.get(0).isEstOuvert());
    }
}
