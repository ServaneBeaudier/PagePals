package com.pagepals.circle.service;

import java.util.List;

import com.pagepals.circle.dto.CircleDTO;
import com.pagepals.circle.dto.CreateCircleDTO;
import com.pagepals.circle.dto.SearchCriteriaDTO;
import com.pagepals.circle.dto.UpdateCircleDTO;

public interface CircleService {

    void createCircle(CreateCircleDTO dto, long createurId);
    void updateCircle(UpdateCircleDTO dto, long createurId);
    void deleteCircleById(Long id);
    void deleteActiveCirclesByCreateur(Long userId);
    void anonymizeUserInArchivedCircles(Long userId);
    CircleDTO getCircleById(long id);
    List<CircleDTO> getCirclesActive();
    List<CircleDTO> getCirclesArchived();
    void archiverCerclesPassés();
    List<CircleDTO> searchCircles(SearchCriteriaDTO criteria);
    List<CircleDTO> findCirclesByCreateur(Long createurId);
}
