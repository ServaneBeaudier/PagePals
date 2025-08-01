package com.pagepals.circle.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pagepals.circle.client.MembershipClient;
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

    private final MembershipClient membershipClient;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void createCircle(CreateCircleDTO dto, long createurId) {
        Circle circle = new Circle();
        circle.setNom(dto.getNom());
        circle.setDateRencontre(dto.getDateRencontre());
        circle.setDateCreation(LocalDate.now());
        circle.setDescription(dto.getDescription());
        circle.setModeRencontre(dto.getModeRencontre());
        circle.setCreateurId(createurId);

        if (dto.getModeRencontre() == ModeRencontre.PRESENTIEL) {
            circle.setLieuRencontre(dto.getLieuRencontre());
            circle.setLieuRencontreDetails(convertDTOToEntity(dto.getLieuRencontreDetails()));
            System.out.println("Lieu rencontre details DTO: " + dto.getLieuRencontreDetails());
        } else {
            circle.setLienVisio(dto.getLienVisio());
            circle.setLieuRencontreDetails(null);
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

        int nbMax = (dto.getNbMaxMembres() != null) ? dto.getNbMaxMembres() : 20;

        if (nbMax < 1 || nbMax > 20) {
            throw new InvalidCircleDataException("Le nombre maximal de membres doit être compris entre 1 et 20.");
        }

        circle.setNbMaxMembres(nbMax);

        if (dto.getLivrePropose() != null) {
            Book book = convertToEntity(dto.getLivrePropose());
            Book savedBook = bookRepository.save(book);
            bookRepository.flush();
            circle.setLivrePropose(savedBook);
        }

        circle = circleRepository.save(circle);
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
        circleExisting.setDescription(dto.getDescription());

        if (ModeRencontre.PRESENTIEL.equals(dto.getModeRencontre())) {
            circleExisting.setLieuRencontre(dto.getLieuRencontre());
            circleExisting.setLieuRencontreDetails(convertDTOToEntity(dto.getLieuRencontreDetails()));
            System.out.println("Lieu rencontre details DTO: " + dto.getLieuRencontreDetails());
            circleExisting.setLienVisio(null);
        } else {
            circleExisting.setLienVisio(dto.getLienVisio());
            circleExisting.setLieuRencontre(null);
            circleExisting.setLieuRencontreDetails(null);
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

        int nbMax = (dto.getNbMaxMembres() != null) ? dto.getNbMaxMembres() : 20;

        if (nbMax < 1 || nbMax > 20) {
            throw new InvalidCircleDataException("Le nombre maximal de membres doit être compris entre 1 et 20.");
        }

        circleExisting.setNbMaxMembres(nbMax);

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
        dto.setLieuRencontreDetails(convertEntityToDTO(circle.getLieuRencontreDetails()));
        dto.setLienVisio(circle.getLienVisio());
        dto.setCreateurId(circle.getCreateurId());
        dto.setNbMaxMembres(circle.getNbMaxMembres());
        dto.setArchived(circle.isArchived());

        dto.setGenreIds(circle.getGenres()
                .stream()
                .map(LiteraryGenre::getId)
                .collect(Collectors.toList()));

        if (circle.getLivrePropose() != null) {
            Book livre = circle.getLivrePropose();
            BookDTO livreDTO = new BookDTO();
            livreDTO.setTitre(livre.getTitre());
            livreDTO.setAuteurs(livre.getAuteurs());
            livreDTO.setIsbn(livre.getIsbn());
            livreDTO.setGenre(livre.getGenre());
            livreDTO.setCouvertureUrl(livre.getCouvertureUrl());
            dto.setLivrePropose(livreDTO);
        } else {
            dto.setLivrePropose(null);
        }

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
            dto.setLieuRencontreDetails(convertEntityToDTO(circle.getLieuRencontreDetails()));
            dto.setLienVisio(circle.getLienVisio());
            dto.setCreateurId(circle.getCreateurId());
            dto.setNbMaxMembres(circle.getNbMaxMembres());
            dto.setArchived(circle.isArchived());

            int membresInscrits = 0;
            try {
                membresInscrits = membershipClient.countMembersForCircle(circle.getId());
            } catch (Exception e) {
                System.err.println("Erreur récupération membres cercle id=" + circle.getId() + ": " + e.getMessage());
            }
            dto.setMembersCount(membresInscrits + 1);

            dto.setGenreIds(circle.getGenres()
                    .stream()
                    .map(LiteraryGenre::getId)
                    .collect(Collectors.toList()));

            if (circle.getLivrePropose() != null) {
                Book livre = circle.getLivrePropose();
                BookDTO livreDTO = new BookDTO();
                livreDTO.setTitre(livre.getTitre());
                livreDTO.setAuteurs(livre.getAuteurs());
                livreDTO.setIsbn(livre.getIsbn());
                livreDTO.setGenre(livre.getGenre());
                livreDTO.setCouvertureUrl(livre.getCouvertureUrl());
                dto.setLivrePropose(livreDTO);
            } else {
                dto.setLivrePropose(null);
            }

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
            dto.setLieuRencontreDetails(convertEntityToDTO(circle.getLieuRencontreDetails()));
            dto.setLienVisio(circle.getLienVisio());
            dto.setCreateurId(circle.getCreateurId());
            dto.setNbMaxMembres(circle.getNbMaxMembres());

            int membresInscrits = 0;
            try {
                membresInscrits = membershipClient.countMembersForCircle(circle.getId());
            } catch (Exception e) {
                System.err.println("Erreur récupération membres cercle id=" + circle.getId() + ": " + e.getMessage());
            }
            dto.setMembersCount(membresInscrits + 1);

            dto.setGenreIds(circle.getGenres()
                    .stream()
                    .map(LiteraryGenre::getId)
                    .collect(Collectors.toList()));

            if (circle.getLivrePropose() != null) {
                Book livre = circle.getLivrePropose();
                BookDTO livreDTO = new BookDTO();
                livreDTO.setTitre(livre.getTitre());
                livreDTO.setAuteurs(livre.getAuteurs());
                livreDTO.setIsbn(livre.getIsbn());
                livreDTO.setGenre(livre.getGenre());
                livreDTO.setCouvertureUrl(livre.getCouvertureUrl());
                dto.setLivrePropose(livreDTO);
            } else {
                dto.setLivrePropose(null);
            }

            return dto;
        }).toList();
    }

    @Scheduled(cron = "0 0 2 * * *") // Tous les jours à 2h du matin
    public void archiverCerclesPasses() {
        List<Circle> cerclesÀArchiver = circleRepository.findByDateRencontreBeforeAndIsArchivedFalse(LocalDate.now());

        for (Circle c : cerclesÀArchiver) {
            c.setArchived(true);
        }

        circleRepository.saveAll(cerclesÀArchiver);
    }

    public Book convertToEntity(BookDTO dto) {
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
        System.out.println("Début de searchCircles avec critères : " + criteria);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Circle> query = cb.createQuery(Circle.class);
        Root<Circle> root = query.from(Circle.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.isFalse(root.get("isArchived")));

        Join<Circle, Book> bookJoin = root.join("livrePropose", JoinType.LEFT);

        if (criteria.getMotCle() != null && !criteria.getMotCle().isBlank()) {
            String pattern = "%" + criteria.getMotCle().toLowerCase() + "%";

            Predicate nameLike = cb.like(cb.lower(root.get("nom")), pattern);
            Predicate descLike = cb.like(cb.lower(root.get("description")), pattern);
            Predicate titleLike = cb.like(cb.lower(bookJoin.get("titre")), pattern);

            // Jointure sur la collection d'auteurs (List<String>)
            ListJoin<Book, String> authorsJoin = bookJoin.joinList("auteurs", JoinType.LEFT);
            Predicate authorLike = cb.like(cb.lower(authorsJoin), pattern);

            // Combine tous les critères mot-clé
            Predicate combinedMotCle = cb.or(nameLike, descLike, titleLike, authorLike);
            predicates.add(combinedMotCle);
        }

        if (criteria.getFormat() != null && !criteria.getFormat().isBlank()) {
            System.out.println("Filtrage format : " + criteria.getFormat());
            predicates.add(cb.equal(cb.lower(root.get("modeRencontre")), criteria.getFormat().toLowerCase()));
        }

        if (criteria.getGenre() != null && !criteria.getGenre().isBlank()) {
            System.out.println("Filtrage genre : " + criteria.getGenre());
            Join<Object, Object> genreJoin = root.join("genres", JoinType.LEFT);
            predicates.add(cb.equal(cb.lower(genreJoin.get("nomGenre")), criteria.getGenre().toLowerCase()));
        }

        if (criteria.getDateExacte() != null) {
            LocalDate date = criteria.getDateExacte();
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
            System.out.println("Filtrage date exacte entre " + startOfDay + " et " + endOfDay);

            predicates.add(cb.between(root.get("dateRencontre"), startOfDay, endOfDay));
        } else if (criteria.getDate() != null) {
            System.out.println("Filtrage date >= " + criteria.getDate());
            predicates.add(cb.greaterThanOrEqualTo(root.get("dateRencontre"), criteria.getDate()));
        }

        query.select(root).distinct(true).where(cb.and(predicates.toArray(new Predicate[0])));

        System.out.println("Exécution de la requête...");
        List<Circle> result = entityManager.createQuery(query).getResultList();
        System.out.println("Nombre de cercles trouvés : " + result.size());

        List<CircleDTO> dtoList = result.stream()
                .map(c -> {
                    System.out.println("Mapping cercle ID: " + c.getId());
                    CircleDTO dto = CircleDTO.builder()
                            .id(c.getId())
                            .nom(c.getNom())
                            .description(c.getDescription())
                            .modeRencontre(c.getModeRencontre())
                            .lieuRencontre(c.getLieuRencontre())
                            .lienVisio(c.getLienVisio())
                            .dateRencontre(c.getDateRencontre())
                            .nbMaxMembres(c.getNbMaxMembres())
                            .genres(c.getGenres().stream()
                                    .map(LiteraryGenre::getNomGenre)
                                    .toList())
                            .build();
                    System.out.println("DTO créé pour cercle ID: " + c.getId());
                    return dto;
                })
                .toList();

        System.out.println("Fin de searchCircles.");
        return dtoList;
    }

    @Override
    public List<CircleDTO> findCirclesByCreateur(Long createurId) {
        List<Circle> circles = circleRepository.findByCreateurId(createurId);

        return circles.stream().map(circle -> {
            CircleDTO dto = new CircleDTO();
            dto.setId(circle.getId());
            dto.setCreateurId(circle.getCreateurId());
            dto.setNom(circle.getNom());
            dto.setDescription(circle.getDescription());
            dto.setDateRencontre(circle.getDateRencontre());
            dto.setDateCreation(circle.getDateCreation());
            dto.setModeRencontre(circle.getModeRencontre());
            dto.setLieuRencontre(circle.getLieuRencontre());
            dto.setLieuRencontreDetails(convertEntityToDTO(circle.getLieuRencontreDetails()));
            dto.setLienVisio(circle.getLienVisio());
            dto.setNbMaxMembres(circle.getNbMaxMembres());
            dto.setArchived(circle.isArchived());

            int membresInscrits = 0;
            try {
                membresInscrits = membershipClient.countMembersForCircle(circle.getId());
            } catch (Exception e) {
                System.err.println("Erreur récupération membres cercle id=" + circle.getId() + ": " + e.getMessage());
            }
            dto.setMembersCount(membresInscrits + 1);

            if (circle.getLivrePropose() != null) {
                Book livre = circle.getLivrePropose();
                BookDTO livreDTO = new BookDTO();
                livreDTO.setTitre(livre.getTitre());
                livreDTO.setAuteurs(livre.getAuteurs());
                livreDTO.setIsbn(livre.getIsbn());
                livreDTO.setGenre(livre.getGenre());
                livreDTO.setCouvertureUrl(livre.getCouvertureUrl());
                dto.setLivrePropose(livreDTO);
            } else {
                dto.setLivrePropose(null);
            }

            dto.setGenreIds(circle.getGenres()
                    .stream()
                    .map(LiteraryGenre::getId)
                    .collect(Collectors.toList()));
                    
            dto.setGenres(circle.getGenres() == null ? List.of()
                    : circle.getGenres().stream()
                            .map(LiteraryGenre::getNomGenre)
                            .toList());

            return dto;
        }).toList();

    }

    @Override
    @Transactional
    public void deleteCircleById(Long id) {
        circleRepository.deleteById(id);
    }

    @Transactional
    public void deleteActiveCirclesByCreateur(Long userId) {
        List<Circle> activeCircles = circleRepository.findByCreateurIdAndIsArchivedFalse(userId);
        circleRepository.deleteAll(activeCircles);
    }

    @Transactional
    public void anonymizeUserInArchivedCircles(Long userId) {
        List<Circle> archivedCircles = circleRepository.findByCreateurIdAndIsArchivedTrue(userId);
        for (Circle circle : archivedCircles) {
            circle.setCreateurId(null);
            circleRepository.save(circle);
        }
    }

    private AdresseDetails convertDTOToEntity(AdresseDetailsDTO dto) {
        if (dto == null)
            return null;
        AdresseDetails entity = new AdresseDetails();
        entity.setShop(dto.getShop());
        entity.setHouseNumber(dto.getHouseNumber());
        entity.setRoad(dto.getRoad());
        entity.setPostcode(dto.getPostcode());
        entity.setCity(dto.getCity());
        return entity;
    }

    private AdresseDetailsDTO convertEntityToDTO(AdresseDetails entity) {
        if (entity == null)
            return null;
        AdresseDetailsDTO dto = new AdresseDetailsDTO();
        dto.setShop(entity.getShop());
        dto.setHouseNumber(entity.getHouseNumber());
        dto.setRoad(entity.getRoad());
        dto.setPostcode(entity.getPostcode());
        dto.setCity(entity.getCity());
        return dto;
    }

}
