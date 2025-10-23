package com.pagepals.circle.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pagepals.circle.dto.BookDTO;
import com.pagepals.circle.dto.SearchBookDTO;
import com.pagepals.circle.service.BookSearchService;

/**
 * Contrôleur REST gérant la recherche de livres à partir de critères fournis par l'utilisateur.
 * 
 * Reçoit les requêtes de recherche et délègue le traitement au service de recherche de livres.
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookSearchService bookSearchService;

    /**
     * Constructeur injectant le service de recherche de livres.
     *
     * @param bookSearchService service responsable de la recherche des ouvrages
     */
    public BookController(BookSearchService bookSearchService) {
        this.bookSearchService = bookSearchService;
    }

    /**
     * Recherche des livres correspondant au critère fourni dans la requête.
     *
     * @param dto objet contenant le critère de recherche
     * @return la liste des livres correspondant au critère
     */
    @PostMapping("/search")
    public ResponseEntity<List<BookDTO>> searchBook(@RequestBody SearchBookDTO dto) {
        List<BookDTO> results = bookSearchService.searchBooks(dto.getCritereRecherche());
        return ResponseEntity.ok(results);
    }
}

