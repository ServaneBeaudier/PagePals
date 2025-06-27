package com.pagepals.circle.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.pagepals.circle.dto.CircleDTO;
import com.pagepals.circle.dto.CreateCircleDTO;
import com.pagepals.circle.dto.UpdateCircleDTO;
import com.pagepals.circle.exception.CircleNotFoundException;
import com.pagepals.circle.exception.InvalidCircleDataException;
import com.pagepals.circle.model.Circle;
import com.pagepals.circle.model.ModeRencontre;
import com.pagepals.circle.repository.CircleRepository;

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

        if (dto.getDateRencontre() != null && dto.getDateRencontre().isBefore(LocalDateTime.now())) {
            throw new InvalidCircleDataException("La date de rencontre ne peut pas être dans le passé.");
        }

        circleRepository.save(circle);
    }

    @Override
    public void updateCircle(UpdateCircleDTO dto) {
        Circle circleExisting = circleRepository.findById(dto.getId())
                .orElseThrow(() -> new CircleNotFoundException("Cercle non trouvé"));

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

        if (dto.getDateRencontre() != null && dto.getDateRencontre().isBefore(LocalDateTime.now())) {
            throw new InvalidCircleDataException("La date de rencontre ne peut pas être dans le passé.");
        }

        circleRepository.save(circleExisting);
    }

    @Override
    public void deleteCircle(long id, long createurId) {
        Circle circleExisting = circleRepository.findById(id)
                .orElseThrow(() -> new CircleNotFoundException("Cercle non trouvé"));
        if (circleExisting.getCreateurId() != createurId) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à supprimer ce cercle.");
        }

        circleRepository.delete(circleExisting);
    }

    @Override
    public CircleDTO getCircleById(long id) {
        Circle circle = circleRepository.findById(id)
                .orElseThrow(() -> new CircleNotFoundException("Cercle non trouvé"));

        CircleDTO dto = new CircleDTO();
        dto.setId(circle.getId());
        dto.setNom(circle.getNom());
        dto.setDescription(circle.getDescription());
        dto.setDateRencontre(circle.getDateRencontre());
        dto.setDateCreation(circle.getDateCreation());
        dto.setModeRencontre(circle.getModeRencontre());
        dto.setLieuRencontre(circle.getLieuRencontre());
        dto.setLienVisio(circle.getLienVisio());
        dto.setCreateurId(circle.getCreateurId());

        return dto;
    }

    @Override
    public List<CircleDTO> getAllCircles() {
        List<Circle> circles = circleRepository.findAll();

        return circles.stream().map(circle -> {
            CircleDTO dto = new CircleDTO();
            dto.setId(circle.getId());
            dto.setNom(circle.getNom());
            dto.setDescription(circle.getDescription());
            dto.setDateRencontre(circle.getDateRencontre());
            dto.setDateCreation(circle.getDateCreation());
            dto.setModeRencontre(circle.getModeRencontre());
            dto.setLieuRencontre(circle.getLieuRencontre());
            dto.setLienVisio(circle.getLienVisio());
            dto.setCreateurId(circle.getCreateurId());

            return dto;
        }).toList();
    }

}
