package com.pagepals.circle.servicetest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.time.*;
import java.util.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pagepals.circle.client.MembershipClient;
import com.pagepals.circle.dto.*;
import com.pagepals.circle.exception.*;
import com.pagepals.circle.model.*;
import com.pagepals.circle.repository.*;
import com.pagepals.circle.service.CircleServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CircleServiceImplTest {

    @Mock
    private CircleRepository circleRepository;

    @Mock
    private LiteraryGenreRepository literaryGenreRepository;

    @Mock
    private BookRepository bookRepository;

    @Spy
    @InjectMocks
    private CircleServiceImpl circleService;

    @Mock
    private MembershipClient membershipClient;

    @Test
    void createCircle_success_presentiel() {
        CreateCircleDTO dto = new CreateCircleDTO();
        dto.setNom("Cercle A");
        dto.setDateRencontre(LocalDateTime.now().plusDays(1));
        dto.setDescription("Description");
        dto.setModeRencontre(ModeRencontre.PRESENTIEL);
        dto.setLieuRencontre("Salle 101");
        dto.setLieuRencontreDetails(new AdresseDetailsDTO(/* remplit les champs */));
        dto.setGenreIds(List.of(1L, 2L));
        dto.setNbMaxMembres(15);
        dto.setLivrePropose(null);

        long createurId = 42L;

        // Mock : pas de cercle existant à cette date pour ce créateur
        when(circleRepository.existsByCreateurIdAndDateRencontre(createurId, dto.getDateRencontre()))
                .thenReturn(false);

        // Mock : genres trouvés
        Set<LiteraryGenre> genres = Set.of(new LiteraryGenre(1L, "Genre1"), new LiteraryGenre(2L, "Genre2"));
        when(literaryGenreRepository.findAllById(dto.getGenreIds()))
                .thenReturn(new ArrayList<>(genres));

        // Mock : sauvegarde circle
        ArgumentCaptor<Circle> circleCaptor = ArgumentCaptor.forClass(Circle.class);
        when(circleRepository.save(any(Circle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        circleService.createCircle(dto, createurId);

        verify(circleRepository).existsByCreateurIdAndDateRencontre(createurId, dto.getDateRencontre());
        verify(literaryGenreRepository).findAllById(dto.getGenreIds());
        verify(circleRepository).save(circleCaptor.capture());

        Circle savedCircle = circleCaptor.getValue();

        assertEquals(dto.getNom(), savedCircle.getNom());
        assertEquals(dto.getDateRencontre(), savedCircle.getDateRencontre());
        assertEquals(LocalDate.now(), savedCircle.getDateCreation());
        assertEquals(dto.getDescription(), savedCircle.getDescription());
        assertEquals(dto.getModeRencontre(), savedCircle.getModeRencontre());
        assertEquals(createurId, savedCircle.getCreateurId());
        assertEquals(dto.getLieuRencontre(), savedCircle.getLieuRencontre());
        assertNotNull(savedCircle.getLieuRencontreDetails()); // vérifier conversion DTO->Entity
        assertEquals(genres, savedCircle.getGenres());
        assertEquals(dto.getNbMaxMembres(), savedCircle.getNbMaxMembres());
        assertNull(savedCircle.getLivrePropose());
    }

    @Test
    void createCircle_success_visio() {
        CreateCircleDTO dto = new CreateCircleDTO();
        dto.setNom("Cercle Visio");
        dto.setDateRencontre(LocalDateTime.now().plusDays(1));
        dto.setDescription("Description visio");
        dto.setModeRencontre(ModeRencontre.ENLIGNE);
        dto.setLienVisio("https://zoom.link");
        dto.setGenreIds(List.of(1L));
        dto.setNbMaxMembres(10);
        dto.setLivrePropose(null);

        long createurId = 42L;

        when(circleRepository.existsByCreateurIdAndDateRencontre(createurId, dto.getDateRencontre()))
                .thenReturn(false);

        Set<LiteraryGenre> genres = Set.of(new LiteraryGenre(1L, "Genre1"));
        when(literaryGenreRepository.findAllById(dto.getGenreIds()))
                .thenReturn(new ArrayList<>(genres));

        ArgumentCaptor<Circle> circleCaptor = ArgumentCaptor.forClass(Circle.class);
        when(circleRepository.save(any(Circle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        circleService.createCircle(dto, createurId);

        verify(circleRepository).existsByCreateurIdAndDateRencontre(createurId, dto.getDateRencontre());
        verify(literaryGenreRepository).findAllById(dto.getGenreIds());
        verify(circleRepository).save(circleCaptor.capture());

        Circle savedCircle = circleCaptor.getValue();

        assertEquals(dto.getNom(), savedCircle.getNom());
        assertEquals(dto.getDateRencontre(), savedCircle.getDateRencontre());
        assertEquals(LocalDate.now(), savedCircle.getDateCreation());
        assertEquals(dto.getDescription(), savedCircle.getDescription());
        assertEquals(dto.getModeRencontre(), savedCircle.getModeRencontre());
        assertEquals(createurId, savedCircle.getCreateurId());
        assertEquals(dto.getLienVisio(), savedCircle.getLienVisio());
        assertNull(savedCircle.getLieuRencontreDetails());
        assertEquals(genres, savedCircle.getGenres());
        assertEquals(dto.getNbMaxMembres(), savedCircle.getNbMaxMembres());
        assertNull(savedCircle.getLivrePropose());
    }

    @Test
    void createCircle_dateRencontrePast_throwsException() {
        CreateCircleDTO dto = new CreateCircleDTO();
        dto.setDateRencontre(LocalDateTime.now().minusDays(1));

        long createurId = 1L;

        InvalidCircleDataException ex = assertThrows(InvalidCircleDataException.class,
                () -> circleService.createCircle(dto, createurId));

        assertEquals("La date de rencontre ne peut pas être dans le passé.", ex.getMessage());
    }

    @Test
    void createCircle_circleAlreadyExists_throwsException() {
        CreateCircleDTO dto = new CreateCircleDTO();
        dto.setDateRencontre(LocalDateTime.now().plusDays(1));
        long createurId = 1L;

        when(circleRepository.existsByCreateurIdAndDateRencontre(createurId, dto.getDateRencontre()))
                .thenReturn(true);

        CircleAlreadyExistsException ex = assertThrows(CircleAlreadyExistsException.class,
                () -> circleService.createCircle(dto, createurId));

        assertEquals("Un cercle a déjà été créé à cette date et heure par cet utilisateur.", ex.getMessage());
    }

    @Test
    void createCircle_invalidGenres_throwsException() {
        CreateCircleDTO dto = new CreateCircleDTO();
        dto.setDateRencontre(LocalDateTime.now().plusDays(1));
        dto.setGenreIds(List.of(1L, 2L));
        long createurId = 1L;

        // Return fewer genres than requested to simulate invalid genre
        when(circleRepository.existsByCreateurIdAndDateRencontre(createurId, dto.getDateRencontre()))
                .thenReturn(false);

        when(literaryGenreRepository.findAllById(dto.getGenreIds()))
                .thenReturn(List.of(new LiteraryGenre(1L, "Genre1"))); // missing genre 2

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> circleService.createCircle(dto, createurId));

        assertEquals("Certains genres sont invalides.", ex.getMessage());
    }

    @Test
    void createCircle_invalidNbMaxMembres_throwsException() {
        CreateCircleDTO dto = new CreateCircleDTO();
        dto.setDateRencontre(LocalDateTime.now().plusDays(1));
        dto.setGenreIds(List.of(1L));
        dto.setNbMaxMembres(0); // invalid < 1
        long createurId = 1L;

        when(circleRepository.existsByCreateurIdAndDateRencontre(createurId, dto.getDateRencontre()))
                .thenReturn(false);

        when(literaryGenreRepository.findAllById(dto.getGenreIds()))
                .thenReturn(List.of(new LiteraryGenre(1L, "Genre1")));

        InvalidCircleDataException ex = assertThrows(InvalidCircleDataException.class,
                () -> circleService.createCircle(dto, createurId));

        assertEquals("Le nombre maximal de membres doit être compris entre 1 et 20.", ex.getMessage());

        // Test > 20
        dto.setNbMaxMembres(21);

        ex = assertThrows(InvalidCircleDataException.class, () -> circleService.createCircle(dto, createurId));

        assertEquals("Le nombre maximal de membres doit être compris entre 1 et 20.", ex.getMessage());
    }

    @Test
    void createCircle_withLivrePropose_savesBook() {
        CreateCircleDTO dto = new CreateCircleDTO();
        dto.setNom("Cercle Livre");
        dto.setDateRencontre(LocalDateTime.now().plusDays(1));
        dto.setDescription("Description");
        dto.setModeRencontre(ModeRencontre.PRESENTIEL);
        dto.setLieuRencontre("Salle 101");
        dto.setLieuRencontreDetails(new AdresseDetailsDTO());
        dto.setGenreIds(List.of(1L));
        dto.setNbMaxMembres(10);

        BookDTO bookDTO = new BookDTO();
        dto.setLivrePropose(bookDTO);

        long createurId = 42L;

        when(circleRepository.existsByCreateurIdAndDateRencontre(createurId, dto.getDateRencontre()))
                .thenReturn(false);

        Set<LiteraryGenre> genres = Set.of(new LiteraryGenre(1L, "Genre1"));
        when(literaryGenreRepository.findAllById(dto.getGenreIds()))
                .thenReturn(new ArrayList<>(genres));

        Book bookEntity = new Book();
        Book savedBook = new Book();
        savedBook.setId(100L);

        // Mock conversion
        when(circleService.convertToEntity(bookDTO)).thenReturn(bookEntity);
        when(bookRepository.save(bookEntity)).thenReturn(savedBook);
        doNothing().when(bookRepository).flush();

        ArgumentCaptor<Circle> circleCaptor = ArgumentCaptor.forClass(Circle.class);
        when(circleRepository.save(any(Circle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        circleService.createCircle(dto, createurId);

        verify(bookRepository).save(bookEntity);
        verify(bookRepository).flush();
        verify(circleRepository).save(circleCaptor.capture());

        Circle savedCircle = circleCaptor.getValue();

        assertEquals(savedBook, savedCircle.getLivrePropose());
    }

    @Test
    void updateCircle_success_presentiel() {
        UpdateCircleDTO dto = new UpdateCircleDTO();
        dto.setId(1L);
        dto.setNom("Nouveau nom");
        dto.setDateRencontre(LocalDateTime.now().plusDays(1));
        dto.setDescription("Desc");
        dto.setModeRencontre(ModeRencontre.PRESENTIEL);
        dto.setLieuRencontre("Salle 1");
        dto.setLieuRencontreDetails(new AdresseDetailsDTO());
        dto.setGenreIds(List.of(1L));
        dto.setNbMaxMembres(10);
        dto.setLivrePropose(null);

        Circle existing = new Circle();
        existing.setId(1L);
        existing.setCreateurId(42L);
        existing.setLivrePropose(null);

        when(circleRepository.findById(dto.getId())).thenReturn(Optional.of(existing));
        when(literaryGenreRepository.findAllById(dto.getGenreIds()))
                .thenReturn(List.of(new LiteraryGenre(1L, "Genre1")));
        when(circleRepository.existsByCreateurIdAndDateRencontreAndIdNot(42L, dto.getDateRencontre(), dto.getId()))
                .thenReturn(false);
        when(circleRepository.save(any(Circle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        circleService.updateCircle(dto, 42L);

        assertEquals(dto.getNom(), existing.getNom());
        assertEquals(dto.getDateRencontre(), existing.getDateRencontre());
        assertEquals(dto.getDescription(), existing.getDescription());
        assertEquals(dto.getModeRencontre(), existing.getModeRencontre());
        assertEquals(dto.getLieuRencontre(), existing.getLieuRencontre());
        assertNotNull(existing.getLieuRencontreDetails());
        assertNull(existing.getLienVisio());
        assertEquals(dto.getNbMaxMembres(), existing.getNbMaxMembres());
        assertEquals(Set.of(new LiteraryGenre(1L, "Genre1")), existing.getGenres());
    }

    @Test
    void updateCircle_success_visio() {
        UpdateCircleDTO dto = new UpdateCircleDTO();
        dto.setId(1L);
        dto.setNom("Nom Visio");
        dto.setDateRencontre(LocalDateTime.now().plusDays(1));
        dto.setDescription("Desc Visio");
        dto.setModeRencontre(ModeRencontre.ENLIGNE);
        dto.setLienVisio("https://meet.link");
        dto.setGenreIds(List.of(1L));
        dto.setNbMaxMembres(15);
        dto.setLivrePropose(null);

        Circle existing = new Circle();
        existing.setId(1L);
        existing.setCreateurId(42L);

        when(circleRepository.findById(dto.getId())).thenReturn(Optional.of(existing));
        when(literaryGenreRepository.findAllById(dto.getGenreIds()))
                .thenReturn(List.of(new LiteraryGenre(1L, "Genre1")));
        when(circleRepository.existsByCreateurIdAndDateRencontreAndIdNot(42L, dto.getDateRencontre(), dto.getId()))
                .thenReturn(false);
        when(circleRepository.save(any(Circle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        circleService.updateCircle(dto, 42L);

        assertEquals(dto.getNom(), existing.getNom());
        assertEquals(dto.getDateRencontre(), existing.getDateRencontre());
        assertEquals(dto.getDescription(), existing.getDescription());
        assertEquals(dto.getModeRencontre(), existing.getModeRencontre());
        assertEquals(dto.getLienVisio(), existing.getLienVisio());
        assertNull(existing.getLieuRencontre());
        assertNull(existing.getLieuRencontreDetails());
        assertEquals(dto.getNbMaxMembres(), existing.getNbMaxMembres());
        assertEquals(Set.of(new LiteraryGenre(1L, "Genre1")), existing.getGenres());
    }

    @Test
    void updateCircle_circleNotFound_throws() {
        UpdateCircleDTO dto = new UpdateCircleDTO();
        dto.setId(999L);

        when(circleRepository.findById(dto.getId())).thenReturn(Optional.empty());

        CircleNotFoundException ex = assertThrows(CircleNotFoundException.class,
                () -> circleService.updateCircle(dto, 42L));

        assertEquals("Cercle non trouvé", ex.getMessage());
    }

    @Test
    void updateCircle_accessDenied_throws() {
        UpdateCircleDTO dto = new UpdateCircleDTO();
        dto.setId(1L);

        Circle existing = new Circle();
        existing.setId(1L);
        existing.setCreateurId(99L);

        when(circleRepository.findById(dto.getId())).thenReturn(Optional.of(existing));

        AccessDeniedException ex = assertThrows(AccessDeniedException.class,
                () -> circleService.updateCircle(dto, 42L));

        assertEquals("Vous n'êtes pas le créateur de ce cercle", ex.getMessage());
    }

    @Test
    void updateCircle_dateInPast_throws() {
        UpdateCircleDTO dto = new UpdateCircleDTO();
        dto.setId(1L);
        dto.setDateRencontre(LocalDateTime.now().minusDays(1));

        Circle existing = new Circle();
        existing.setId(1L);
        existing.setCreateurId(42L);

        when(circleRepository.findById(dto.getId())).thenReturn(Optional.of(existing));

        InvalidCircleDataException ex = assertThrows(InvalidCircleDataException.class,
                () -> circleService.updateCircle(dto, 42L));

        assertEquals("La date de rencontre ne peut pas être dans le passé.", ex.getMessage());
    }

    @Test
    void updateCircle_invalidGenres_throws() {
        UpdateCircleDTO dto = new UpdateCircleDTO();
        dto.setId(1L);
        dto.setGenreIds(List.of(1L, 2L));

        Circle existing = new Circle();
        existing.setId(1L);
        existing.setCreateurId(42L);

        when(circleRepository.findById(dto.getId())).thenReturn(Optional.of(existing));
        when(literaryGenreRepository.findAllById(dto.getGenreIds()))
                .thenReturn(List.of(new LiteraryGenre(1L, "Genre1")));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> circleService.updateCircle(dto, 42L));

        assertEquals("Certains genres sont invalides.", ex.getMessage());
    }

    @Test
    void updateCircle_invalidNbMax_throws() {
        UpdateCircleDTO dto = new UpdateCircleDTO();
        dto.setId(1L);
        dto.setGenreIds(List.of(1L));
        dto.setNbMaxMembres(0);

        Circle existing = new Circle();
        existing.setId(1L);
        existing.setCreateurId(42L);

        when(circleRepository.findById(dto.getId())).thenReturn(Optional.of(existing));
        when(literaryGenreRepository.findAllById(dto.getGenreIds()))
                .thenReturn(List.of(new LiteraryGenre(1L, "Genre1")));

        InvalidCircleDataException ex = assertThrows(InvalidCircleDataException.class,
                () -> circleService.updateCircle(dto, 42L));

        assertEquals("Le nombre maximal de membres doit être compris entre 1 et 20.", ex.getMessage());

        // test > 20
        dto.setNbMaxMembres(21);
        ex = assertThrows(InvalidCircleDataException.class, () -> circleService.updateCircle(dto, 42L));

        assertEquals("Le nombre maximal de membres doit être compris entre 1 et 20.", ex.getMessage());
    }

    @Test
    void updateCircle_livreProposeUpdated_deletesOldBook() {
        UpdateCircleDTO dto = new UpdateCircleDTO();
        dto.setId(1L);
        dto.setGenreIds(List.of(1L));
        dto.setLivrePropose(new BookDTO());
        dto.setNbMaxMembres(10);

        Circle existing = new Circle();
        existing.setId(1L);
        existing.setCreateurId(42L);

        Book oldBook = new Book();
        oldBook.setId(10L);
        existing.setLivrePropose(oldBook);

        Book newBook = new Book();
        newBook.setId(20L);

        when(circleRepository.findById(dto.getId())).thenReturn(Optional.of(existing));
        when(literaryGenreRepository.findAllById(dto.getGenreIds()))
                .thenReturn(List.of(new LiteraryGenre(1L, "Genre1")));

        doReturn(newBook).when(circleService).convertToEntity(dto.getLivrePropose());
        when(bookRepository.save(any(Book.class))).thenReturn(newBook);
        doNothing().when(bookRepository).flush();

        when(circleRepository.existsByCreateurIdAndDateRencontreAndIdNot(42L, dto.getDateRencontre(), dto.getId()))
                .thenReturn(false);

        when(circleRepository.save(any(Circle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        circleService.updateCircle(dto, 42L);

        verify(bookRepository).delete(oldBook);
    }

    @Test
    void updateCircle_circleAlreadyExists_throws() {
        UpdateCircleDTO dto = new UpdateCircleDTO();
        dto.setId(1L);
        dto.setGenreIds(List.of(1L));

        Circle existing = new Circle();
        existing.setId(1L);
        existing.setCreateurId(42L);
        dto.setDateRencontre(LocalDateTime.now().plusDays(1));

        when(circleRepository.findById(dto.getId())).thenReturn(Optional.of(existing));
        when(literaryGenreRepository.findAllById(dto.getGenreIds()))
                .thenReturn(List.of(new LiteraryGenre(1L, "Genre1")));

        when(circleRepository.existsByCreateurIdAndDateRencontreAndIdNot(42L, dto.getDateRencontre(), dto.getId()))
                .thenReturn(true);

        CircleAlreadyExistsException ex = assertThrows(CircleAlreadyExistsException.class,
                () -> circleService.updateCircle(dto, 42L));

        assertEquals("Un cercle a déjà été créé à cette date et heure par cet utilisateur.", ex.getMessage());
    }

    @Test
    void getCircleById_success_withLivrePropose() {
        long circleId = 1L;

        Book livre = new Book();
        livre.setTitre("Titre Livre");
        livre.setAuteurs(List.of("Auteur1"));
        livre.setIsbn("1234567890");
        livre.setGenre("Roman");
        livre.setCouvertureUrl("http://image.url");

        Circle circle = new Circle();
        circle.setId(circleId);
        circle.setNom("Cercle A");
        circle.setDescription("Desc");
        circle.setDateRencontre(LocalDateTime.now().plusDays(1));
        circle.setDateCreation(LocalDate.now());
        circle.setModeRencontre(ModeRencontre.PRESENTIEL);
        circle.setLieuRencontre("Salle 101");
        circle.setLieuRencontreDetails(new AdresseDetails()); // ou mock si nécessaire
        circle.setLienVisio(null);
        circle.setCreateurId(42L);
        circle.setNbMaxMembres(15);
        circle.setArchived(false);
        circle.setGenres(Set.of(new LiteraryGenre(1L, "Genre1"), new LiteraryGenre(2L, "Genre2")));
        circle.setLivrePropose(livre);

        when(circleRepository.findById(circleId)).thenReturn(Optional.of(circle));

        CircleDTO dto = circleService.getCircleById(circleId);

        assertEquals(circleId, dto.getId());
        assertEquals(circle.getNom(), dto.getNom());
        assertEquals(circle.getDescription(), dto.getDescription());
        assertEquals(circle.getDateRencontre(), dto.getDateRencontre());
        assertEquals(circle.getDateCreation(), dto.getDateCreation());
        assertEquals(circle.getModeRencontre(), dto.getModeRencontre());
        assertEquals(circle.getLieuRencontre(), dto.getLieuRencontre());
        assertNotNull(dto.getLieuRencontreDetails());
        assertEquals(circle.getLienVisio(), dto.getLienVisio());
        assertEquals(circle.getCreateurId(), dto.getCreateurId());
        assertEquals(circle.getNbMaxMembres(), dto.getNbMaxMembres());
        assertEquals(circle.isArchived(), dto.isArchived());

        assertEquals(2, dto.getGenreIds().size());
        assertTrue(dto.getGenreIds().contains(1L));
        assertTrue(dto.getGenreIds().contains(2L));

        assertNotNull(dto.getLivrePropose());
        assertEquals(livre.getTitre(), dto.getLivrePropose().getTitre());
        assertEquals(livre.getAuteurs(), dto.getLivrePropose().getAuteurs());
        assertEquals(livre.getIsbn(), dto.getLivrePropose().getIsbn());
        assertEquals(livre.getGenre(), dto.getLivrePropose().getGenre());
        assertEquals(livre.getCouvertureUrl(), dto.getLivrePropose().getCouvertureUrl());
    }

    @Test
    void getCircleById_success_withoutLivrePropose() {
        long circleId = 2L;

        Circle circle = new Circle();
        circle.setId(circleId);
        circle.setNom("Cercle B");
        circle.setLivrePropose(null);

        when(circleRepository.findById(circleId)).thenReturn(Optional.of(circle));

        CircleDTO dto = circleService.getCircleById(circleId);

        assertEquals(circleId, dto.getId());
        assertEquals(circle.getNom(), dto.getNom());
        assertNull(dto.getLivrePropose());
    }

    @Test
    void getCircleById_notFound_throws() {
        long circleId = 3L;

        when(circleRepository.findById(circleId)).thenReturn(Optional.empty());

        CircleNotFoundException ex = assertThrows(CircleNotFoundException.class,
                () -> circleService.getCircleById(circleId));

        assertEquals("Cercle non trouvé", ex.getMessage());
    }

    @Test
    void getCirclesActive_returnsListWithMembersCount() {
        Circle circle = new Circle();
        circle.setId(1L);
        circle.setNom("Cercle 1");
        circle.setDescription("Desc");
        circle.setDateRencontre(LocalDateTime.now().plusDays(1));
        circle.setDateCreation(LocalDate.now());
        circle.setModeRencontre(ModeRencontre.PRESENTIEL);
        circle.setLieuRencontre("Lieu 1");
        circle.setLieuRencontreDetails(new AdresseDetails());
        circle.setLienVisio(null);
        circle.setCreateurId(10L);
        circle.setNbMaxMembres(15);
        circle.setGenres(Set.of(new LiteraryGenre(1L, "Genre1")));
        circle.setLivrePropose(null);

        when(circleRepository.findByIsArchivedFalse()).thenReturn(List.of(circle));
        when(membershipClient.countMembersForCircle(circle.getId())).thenReturn(4);

        List<CircleDTO> result = circleService.getCirclesActive();

        assertEquals(1, result.size());
        CircleDTO dto = result.get(0);
        assertEquals(circle.getId(), dto.getId());
        assertEquals(circle.getNom(), dto.getNom());
        assertEquals(5, dto.getMembersCount()); // 4 + 1

        assertNull(dto.getLivrePropose());
        assertEquals(1, dto.getGenreIds().size());
    }

    @Test
    void getCirclesActive_emptyList_returnsEmptyList() {
        when(circleRepository.findByIsArchivedFalse()).thenReturn(Collections.emptyList());

        List<CircleDTO> result = circleService.getCirclesActive();

        assertTrue(result.isEmpty());
    }

    @Test
    void getCirclesActive_membershipClientThrows_exceptionHandled() {
        Circle circle = new Circle();
        circle.setId(1L);
        circle.setNom("Cercle 1");
        circle.setGenres(Set.of(new LiteraryGenre(1L, "Genre1")));

        when(circleRepository.findByIsArchivedFalse()).thenReturn(List.of(circle));
        when(membershipClient.countMembersForCircle(circle.getId())).thenThrow(new RuntimeException("Client error"));

        List<CircleDTO> result = circleService.getCirclesActive();

        assertEquals(1, result.size());
        CircleDTO dto = result.get(0);
        assertEquals(1, dto.getMembersCount()); // default +1 car exception
    }

    @Test
    void getCirclesArchived_returnsListWithMembersCount() {
        Circle circle = new Circle();
        circle.setId(1L);
        circle.setNom("Cercle Archivé");
        circle.setDescription("Desc Archivé");
        circle.setDateRencontre(LocalDateTime.now().plusDays(1));
        circle.setDateCreation(LocalDate.now());
        circle.setModeRencontre(ModeRencontre.PRESENTIEL);
        circle.setLieuRencontre("Lieu Archivé");
        circle.setLieuRencontreDetails(new AdresseDetails());
        circle.setLienVisio(null);
        circle.setCreateurId(10L);
        circle.setNbMaxMembres(15);
        circle.setGenres(Set.of(new LiteraryGenre(1L, "Genre1")));
        circle.setLivrePropose(null);

        when(circleRepository.findByIsArchivedTrue()).thenReturn(List.of(circle));
        when(membershipClient.countMembersForCircle(circle.getId())).thenReturn(7);

        List<CircleDTO> result = circleService.getCirclesArchived();

        assertEquals(1, result.size());
        CircleDTO dto = result.get(0);
        assertEquals(circle.getId(), dto.getId());
        assertEquals(circle.getNom(), dto.getNom());
        assertEquals(8, dto.getMembersCount()); // 7 + 1

        assertNull(dto.getLivrePropose());
        assertEquals(1, dto.getGenreIds().size());
    }

    @Test
    void getCirclesArchived_emptyList_returnsEmptyList() {
        when(circleRepository.findByIsArchivedTrue()).thenReturn(Collections.emptyList());

        List<CircleDTO> result = circleService.getCirclesArchived();

        assertTrue(result.isEmpty());
    }

    @Test
    void getCirclesArchived_membershipClientThrows_exceptionHandled() {
        Circle circle = new Circle();
        circle.setId(1L);
        circle.setNom("Cercle Archivé");
        circle.setGenres(Set.of(new LiteraryGenre(1L, "Genre1")));

        when(circleRepository.findByIsArchivedTrue()).thenReturn(List.of(circle));
        when(membershipClient.countMembersForCircle(circle.getId())).thenThrow(new RuntimeException("Client error"));

        List<CircleDTO> result = circleService.getCirclesArchived();

        assertEquals(1, result.size());
        CircleDTO dto = result.get(0);
        assertEquals(1, dto.getMembersCount()); // default +1 car exception
    }

    @Test
    void archiverCerclesPasses_archivesCorrectement() {
        Circle c1 = new Circle();
        c1.setArchived(false);
        Circle c2 = new Circle();
        c2.setArchived(false);

        List<Circle> toArchive = List.of(c1, c2);

        when(circleRepository.findByDateRencontreBeforeAndIsArchivedFalse(LocalDate.now()))
                .thenReturn(toArchive);

        circleService.archiverCerclesPasses();

        assertTrue(c1.isArchived());
        assertTrue(c2.isArchived());

        verify(circleRepository).saveAll(toArchive);
    }

    @Test
    void archiverCerclesPasses_aucunCercle_rienNeSePasse() {
        when(circleRepository.findByDateRencontreBeforeAndIsArchivedFalse(LocalDate.now()))
                .thenReturn(Collections.emptyList());

        circleService.archiverCerclesPasses();

        verify(circleRepository).saveAll(Collections.emptyList());
    }

    @Test
    void convertToEntity_mapsFieldsCorrectly() {
        BookDTO dto = new BookDTO();
        dto.setTitre("Titre Livre");
        dto.setAuteurs(List.of("Auteur1", "Auteur2"));
        dto.setGenre("Roman");
        dto.setIsbn("1234567890");
        dto.setCouvertureUrl("http://image.url");

        Book book = circleService.convertToEntity(dto);

        assertEquals(dto.getTitre(), book.getTitre());
        assertEquals(dto.getAuteurs(), book.getAuteurs());
        assertEquals(dto.getGenre(), book.getGenre());
        assertEquals(dto.getIsbn(), book.getIsbn());
        assertEquals(dto.getCouvertureUrl(), book.getCouvertureUrl());
    }

    @Test
    void findCirclesByCreateur_returnsDtosWithMembersCount() {
        Circle circle = new Circle();
        circle.setId(1L);
        circle.setCreateurId(42L);
        circle.setNom("Cercle du créateur");
        circle.setDescription("Description cercle");
        circle.setDateRencontre(LocalDateTime.now().plusDays(1));
        circle.setDateCreation(LocalDate.now());
        circle.setModeRencontre(ModeRencontre.PRESENTIEL);
        circle.setLieuRencontre("Chez moi");
        circle.setLieuRencontreDetails(new AdresseDetails());
        circle.setLienVisio(null);
        circle.setNbMaxMembres(15);
        circle.setGenres(Set.of(new LiteraryGenre(1L, "Genre1")));

        // Pas de livre proposé
        circle.setLivrePropose(null);

        when(circleRepository.findByCreateurId(42L)).thenReturn(List.of(circle));
        when(membershipClient.countMembersForCircle(1L)).thenReturn(3);

        List<CircleDTO> result = circleService.findCirclesByCreateur(42L);

        assertEquals(1, result.size());
        CircleDTO dto = result.get(0);

        assertEquals(circle.getId(), dto.getId());
        assertEquals(circle.getCreateurId(), dto.getCreateurId());
        assertEquals(circle.getNom(), dto.getNom());
        assertEquals(4, dto.getMembersCount()); // 3 + 1
        assertEquals(1, dto.getGenres().size());
        assertEquals("Genre1", dto.getGenres().get(0));
        assertNull(dto.getLivrePropose());
    }

    @Test
    void findCirclesByCreateur_returnsEmptyListWhenNone() {
        when(circleRepository.findByCreateurId(99L)).thenReturn(Collections.emptyList());

        List<CircleDTO> result = circleService.findCirclesByCreateur(99L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findCirclesByCreateur_membershipClientThrows_setsMembersCountToOne() {
        Circle circle = new Circle();
        circle.setId(2L);
        circle.setCreateurId(42L);
        circle.setGenres(Set.of(new LiteraryGenre(1L, "Genre1")));
        circle.setLivrePropose(null);

        when(circleRepository.findByCreateurId(42L)).thenReturn(List.of(circle));
        when(membershipClient.countMembersForCircle(2L)).thenThrow(new RuntimeException("Erreur"));

        List<CircleDTO> result = circleService.findCirclesByCreateur(42L);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getMembersCount()); // par défaut +1 en cas d’erreur
    }

    @Test
    void findCirclesByCreateur_handlesNullGenresAndLivrePropose() {
        Circle circle = new Circle();
        circle.setId(3L);
        circle.setCreateurId(42L);
        circle.setGenres(null);
        circle.setLivrePropose(null);

        when(circleRepository.findByCreateurId(42L)).thenReturn(List.of(circle));
        when(membershipClient.countMembersForCircle(3L)).thenReturn(0);

        List<CircleDTO> result = circleService.findCirclesByCreateur(42L);

        assertEquals(1, result.size());
        assertNotNull(result.get(0).getGenres());
        assertTrue(result.get(0).getGenres().isEmpty());
        assertNull(result.get(0).getLivrePropose());
        assertEquals(1, result.get(0).getMembersCount());
    }

    @Test
    void deleteCircleById_callsRepositoryDelete() {
        Long circleId = 42L;

        circleService.deleteCircleById(circleId);

        verify(circleRepository, times(1)).deleteById(circleId);
    }

    @Test
    void deleteActiveCirclesByCreateur_callsFindAndDeleteAll() {
        Long userId = 100L;

        Circle circle1 = new Circle();
        circle1.setId(1L);
        Circle circle2 = new Circle();
        circle2.setId(2L);

        List<Circle> activeCircles = List.of(circle1, circle2);

        when(circleRepository.findByCreateurIdAndIsArchivedFalse(userId)).thenReturn(activeCircles);

        circleService.deleteActiveCirclesByCreateur(userId);

        verify(circleRepository, times(1)).findByCreateurIdAndIsArchivedFalse(userId);
        verify(circleRepository, times(1)).deleteAll(activeCircles);
    }

    @Test
    void deleteActiveCirclesByCreateur_emptyList_callsDeleteAllWithEmptyList() {
        Long userId = 101L;

        when(circleRepository.findByCreateurIdAndIsArchivedFalse(userId)).thenReturn(Collections.emptyList());

        circleService.deleteActiveCirclesByCreateur(userId);

        verify(circleRepository, times(1)).findByCreateurIdAndIsArchivedFalse(userId);
        verify(circleRepository, times(1)).deleteAll(Collections.emptyList());
    }

    @Test
    void anonymizeUserInArchivedCircles_circlesFound_anonymizeAndSave() {
        Long userId = 42L;

        Circle c1 = new Circle();
        c1.setId(1L);
        c1.setCreateurId(userId);

        Circle c2 = new Circle();
        c2.setId(2L);
        c2.setCreateurId(userId);

        List<Circle> archivedCircles = List.of(c1, c2);

        when(circleRepository.findByCreateurIdAndIsArchivedTrue(userId)).thenReturn(archivedCircles);

        circleService.anonymizeUserInArchivedCircles(userId);

        assertNull(c1.getCreateurId());
        assertNull(c2.getCreateurId());

        verify(circleRepository, times(1)).findByCreateurIdAndIsArchivedTrue(userId);
        verify(circleRepository, times(1)).save(c1);
        verify(circleRepository, times(1)).save(c2);
    }

    @Test
    void anonymizeUserInArchivedCircles_noCircles_noSaveCalled() {
        Long userId = 100L;

        when(circleRepository.findByCreateurIdAndIsArchivedTrue(userId)).thenReturn(Collections.emptyList());

        circleService.anonymizeUserInArchivedCircles(userId);

        verify(circleRepository, times(1)).findByCreateurIdAndIsArchivedTrue(userId);
        verify(circleRepository, never()).save(any());
    }

    @Test
    void convertDTOToEntity_returnsNullWhenDtoIsNull() throws Exception {
        AdresseDetails result = invokeConvertDTOToEntity(null);
        assertNull(result);
    }

    @Test
    void convertDTOToEntity_mapsFieldsCorrectly() throws Exception {
        AdresseDetailsDTO dto = new AdresseDetailsDTO();
        dto.setShop("ShopName");
        dto.setHouseNumber("123");
        dto.setRoad("Main Road");
        dto.setPostcode("75000");
        dto.setCity("Paris");

        AdresseDetails entity = invokeConvertDTOToEntity(dto);

        assertEquals(dto.getShop(), entity.getShop());
        assertEquals(dto.getHouseNumber(), entity.getHouseNumber());
        assertEquals(dto.getRoad(), entity.getRoad());
        assertEquals(dto.getPostcode(), entity.getPostcode());
        assertEquals(dto.getCity(), entity.getCity());
    }

    @Test
    void convertEntityToDTO_returnsNullWhenEntityIsNull() throws Exception {
        AdresseDetailsDTO result = invokeConvertEntityToDTO(null);
        assertNull(result);
    }

    @Test
    void convertEntityToDTO_mapsFieldsCorrectly() throws Exception {
        AdresseDetails entity = new AdresseDetails();
        entity.setShop("ShopName");
        entity.setHouseNumber("123");
        entity.setRoad("Main Road");
        entity.setPostcode("75000");
        entity.setCity("Paris");

        AdresseDetailsDTO dto = invokeConvertEntityToDTO(entity);

        assertEquals(entity.getShop(), dto.getShop());
        assertEquals(entity.getHouseNumber(), dto.getHouseNumber());
        assertEquals(entity.getRoad(), dto.getRoad());
        assertEquals(entity.getPostcode(), dto.getPostcode());
        assertEquals(entity.getCity(), dto.getCity());
    }

    private AdresseDetails invokeConvertDTOToEntity(AdresseDetailsDTO dto) throws Exception {
        Method method = CircleServiceImpl.class.getDeclaredMethod("convertDTOToEntity", AdresseDetailsDTO.class);
        method.setAccessible(true); // rend la méthode accessible même si privée
        return (AdresseDetails) method.invoke(circleService, dto);
    }

    private AdresseDetailsDTO invokeConvertEntityToDTO(AdresseDetails entity) throws Exception {
        Method method = CircleServiceImpl.class.getDeclaredMethod("convertEntityToDTO", AdresseDetails.class);
        method.setAccessible(true);
        return (AdresseDetailsDTO) method.invoke(circleService, entity);
    }

}
