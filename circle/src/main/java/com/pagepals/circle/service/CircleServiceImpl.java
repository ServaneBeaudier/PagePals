package com.pagepals.circle.service;

import java.time.LocalDate;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.pagepals.circle.dto.CreateCircleDTO;
import com.pagepals.circle.dto.UpdateCircleDTO;
import com.pagepals.circle.model.Circle;
import com.pagepals.circle.model.ModeRencontre;
import com.pagepals.circle.repository.CircleRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CircleServiceImpl implements CircleService {

    private final CircleRepository circleRepository;

    @Override
    public void createCircle(CreateCircleDTO dto, long createurId) {
        Circle circle = new Circle();
        circle.setNom(dto.getNom());
        circle.setDateRencontre(dto.getDateRencontre());
        circle.setDateCreation(LocalDate.now());
        circle.setModeRencontre(dto.getModeRencontre());
        circle.setCreateurId(createurId);

        if (dto.getModeRencontre() == ModeRencontre.PRESENTIEL) {
            circle.setLieuRencontre(dto.getLieuRencontre());
        } else {
            circle.setLienVisio(dto.getLienVisio());
        }

        circleRepository.save(circle);
    }

    @Override
    public void updateCircle(UpdateCircleDTO dto) {
        Circle circleExisting = circleRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Cercle non trouvé"));

        circleExisting.setNom(dto.getNom());
        circleExisting.setDateRencontre(dto.getDateRencontre());
        circleExisting.setModeRencontre(dto.getModeRencontre());

        if (ModeRencontre.PRESENTIEL.equals(dto.getModeRencontre())) {
            circleExisting.setLieuRencontre(dto.getLieuRencontre());
            circleExisting.setLienVisio(null);
        } else {
            circleExisting.setLienVisio(dto.getLienVisio());
            circleExisting.setLieuRencontre(null);
        }

        circleRepository.save(circleExisting);
    }

    @Override
    public void deleteCircle(long id, long createurId) {
        Circle circleExisting = circleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cercle non trouvé"));

        if (circleExisting.getCreateurId() != createurId) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à supprimer ce cercle.");
        }

        circleRepository.delete(circleExisting);
    }

}
