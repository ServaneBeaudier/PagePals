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

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookSearchService bookSearchService;

    public BookController(BookSearchService bookSearchService) {
        this.bookSearchService = bookSearchService;
    }

    @PostMapping("/search")
    public ResponseEntity<List<BookDTO>> searchBook(@RequestBody SearchBookDTO dto) {
        System.out.println(">>> Appel reçu dans BookController <<<");
        List<BookDTO> results = bookSearchService.searchBooks(dto.getCritereRecherche());
        return ResponseEntity.ok(results);
    }
}
