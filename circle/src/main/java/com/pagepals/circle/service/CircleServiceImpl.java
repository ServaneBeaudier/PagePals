package com.pagepals.circle.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.pagepals.circle.dto.BookDTO;
import com.pagepals.circle.dto.CircleDTO;
import com.pagepals.circle.dto.CreateCircleDTO;
import com.pagepals.circle.dto.UpdateCircleDTO;
import com.pagepals.circle.exception.AccessDeniedException;
import com.pagepals.circle.exception.CircleAlreadyExistsException;
import com.pagepals.circle.exception.CircleNotFoundException;
import com.pagepals.circle.exception.InvalidCircleDataException;
import com.pagepals.circle.model.Book;
import com.pagepals.circle.model.Circle;
import com.pagepals.circle.model.ModeRencontre;
import com.pagepals.circle.repository.BookRepository;
import com.pagepals.circle.repository.CircleRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CircleServiceImpl implements CircleService {

    private final CircleRepository circleRepository;

    private final BookRepository bookRepository;

    @Override
    @Transactional
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

        boolean exists = circleRepository.existsByCreateurIdAndDateRencontre(createurId,
                dto.getDateRencontre());
        if (exists) {
            throw new CircleAlreadyExistsException(
                    "Un cercle a déjà été créé à cette date et heure par cet utilisateur.");
        }

        if (dto.getLivrePropose() != null) {
            Book book = convertToEntity(dto.getLivrePropose());
            Book savedBook = bookRepository.save(book);
            bookRepository.flush();
            circle.setLivrePropose(savedBook);
        }

        circleRepository.save(circle);
    }

    @Override
    @Transactional
    public void updateCircle(UpdateCircleDTO dto, long createurId) {
        Circle circleExisting = circleRepository.findById(dto.getId())
                .orElseThrow(() -> new CircleNotFoundException("Cercle non trouvé"));

        if (!Objects.equals(circleExisting.getCreateurId(), createurId)) {
            throw new AccessDeniedException("Vous n'êtes pas le créateur de ce cercle");
        }

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

        if (dto.getLivrePropose() != null) {
            Book ancienLivre = circleExisting.getLivrePropose();

            Book book = convertToEntity(dto.getLivrePropose());
            Book savedBook = bookRepository.save(book);
            bookRepository.flush();
            circleExisting.setLivrePropose(savedBook);

            if (ancienLivre != null && ancienLivre.getId() != savedBook.getId()) {
                bookRepository.delete(ancienLivre);
            }
        }

        boolean exists = circleRepository.existsByCreateurIdAndDateRencontreAndIdNot(
                circleExisting.getCreateurId(),
                dto.getDateRencontre(),
                dto.getId());

        if (exists) {
            throw new CircleAlreadyExistsException(
                    "Un cercle a déjà été créé à cette date et heure par cet utilisateur.");
        }

        circleRepository.save(circleExisting);
    }

    @Override
    @Transactional
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
    public List<CircleDTO> getCirclesActive() {
        List<Circle> circles = circleRepository.findByIsArchivedFalse();

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

    @Override
    public List<CircleDTO> getCirclesArchived() {
        List<Circle> circles = circleRepository.findByIsArchivedTrue();

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

    @Scheduled(cron = "0 0 2 * * *") // Tous les jours à 2h du matin
    public void archiverCerclesPassés() {
        List<Circle> cerclesÀArchiver = circleRepository.findByDateRencontreBeforeAndIsArchivedFalse(LocalDate.now());

        for (Circle c : cerclesÀArchiver) {
            c.setArchived(true);
        }

        circleRepository.saveAll(cerclesÀArchiver);
    }

    private Book convertToEntity(BookDTO dto) {
        Book book = new Book();
        book.setTitre(dto.getTitre());
        book.setAuteurs(dto.getAuteurs());
        book.setGenre(dto.getGenre());
        book.setIsbn(dto.getIsbn());
        book.setCouvertureUrl(dto.getCouvertureUrl());
        return book;
    }

}
