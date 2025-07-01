package com.pagepals.circle.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pagepals.circle.dto.*;
import com.pagepals.circle.exception.*;
import com.pagepals.circle.model.*;
import com.pagepals.circle.repository.*;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CircleServiceImpl implements CircleService {

    private final CircleRepository circleRepository;

    private final BookRepository bookRepository;

    private final LiteraryGenreRepository literaryGenreRepository;

    @PersistenceContext
    private EntityManager entityManager;

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

        List<Long> genreIds = dto.getGenreIds();
        Set<LiteraryGenre> genres = new HashSet<>(literaryGenreRepository.findAllById(genreIds));

        if (genres.size() != genreIds.size()) {
            throw new IllegalArgumentException("Certains genres sont invalides.");
        }

        circle.setGenres(genres);

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

        List<Long> genreIds = dto.getGenreIds();
        Set<LiteraryGenre> genres = new HashSet<>(literaryGenreRepository.findAllById(genreIds));
        if (genres.size() != genreIds.size()) {
            throw new IllegalArgumentException("Certains genres sont invalides.");
        }

        circleExisting.setGenres(genres);

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

    @Override
    @Transactional(readOnly = true)
    public List<CircleDTO> searchCircles(SearchCriteriaDTO criteria) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Circle> query = cb.createQuery(Circle.class);
        Root<Circle> root = query.from(Circle.class);
        List<Predicate> predicates = new ArrayList<>();

        if (criteria.getMotCle() != null && !criteria.getMotCle().isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("nom")), "%" + criteria.getMotCle().toLowerCase() + "%"));
        }

        if (criteria.getFormat() != null && !criteria.getFormat().isBlank()) {
            predicates.add(cb.equal(cb.lower(root.get("format")), criteria.getFormat().toLowerCase()));
        }

        if (criteria.getGenre() != null && !criteria.getGenre().isBlank()) {
            // À adapter selon ta relation réelle (ManyToMany ou autre)
            Join<Object, Object> genreJoin = root.join("genres", JoinType.LEFT);
            predicates.add(cb.equal(cb.lower(genreJoin.get("nomGenre")), criteria.getGenre().toLowerCase()));
        }

        query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
        List<Circle> result = entityManager.createQuery(query).getResultList();

        return result.stream()
                .map(c -> CircleDTO.builder()
                        .id(c.getId())
                        .nom(c.getNom())
                        .description(c.getDescription())
                        .modeRencontre(c.getModeRencontre())
                        .lieuRencontre(c.getLieuRencontre())
                        .lienVisio(c.getLienVisio())
                        .dateRencontre(c.getDateRencontre())
                        .genres(c.getGenres().stream()
                                .map(LiteraryGenre::getNomGenre)
                                .toList())
                        .build())
                .toList();
    }

}
