package com.pagepals.search.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pagepals.search.client.CircleClient;
import com.pagepals.search.client.MembershipClient;
import com.pagepals.search.dto.CircleDTO;
import com.pagepals.search.dto.SearchCriteriaDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final CircleClient circleClient;

    private final MembershipClient membershipClient;

    @Override
    public List<CircleDTO> searchCircles(SearchCriteriaDTO criteria) {
        List<CircleDTO> cerclesBruts = circleClient.searchCircles(criteria);

        List<CircleDTO> cerclesEnrichis = cerclesBruts.stream()
                .map(c -> {
                    int nbInscrits = membershipClient.getNombreInscrits(c.getId());
                    boolean estOuvert = nbInscrits < c.getNbMaxMembres();

                    return CircleDTO.builder()
                            .id(c.getId())
                            .nom(c.getNom())
                            .dateRencontre(c.getDateRencontre())
                            .modeRencontre(c.getModeRencontre())
                            .genres(c.getGenres())
                            .nbMaxMembres(c.getNbMaxMembres())
                            .nombreInscrits(nbInscrits)
                            .estOuvert(estOuvert)
                            .build();
                })
                .toList();

        if (criteria.getEstOuvert() != null) {
            cerclesEnrichis = cerclesEnrichis.stream()
                    .filter(c -> c.isEstOuvert() == criteria.getEstOuvert())
                    .toList();
        }

        return cerclesEnrichis;
    }
}
