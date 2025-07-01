package com.pagepals.circle.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pagepals.circle.model.LiteraryGenre;
import com.pagepals.circle.repository.LiteraryGenreRepository;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

     private final LiteraryGenreRepository genreRepository;

    public GenreController(LiteraryGenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @GetMapping
    public List<String> getAllGenres() {
        return genreRepository.findAll().stream()
                .map(LiteraryGenre::getNomGenre)
                .toList();
    }
}
