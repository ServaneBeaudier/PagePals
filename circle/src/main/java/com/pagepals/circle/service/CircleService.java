package com.pagepals.circle.service;

import com.pagepals.circle.dto.CreateCircleDTO;
import com.pagepals.circle.dto.UpdateCircleDTO;

public interface CircleService {

    void createCircle(CreateCircleDTO dto, long createurId);
    void updateCircle(UpdateCircleDTO dto);
    void deleteCircle(long id, long createurId);

}
