package com.pagepals.circle.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pagepals.circle.repository.LiteraryGenreRepository;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    private final LiteraryGenreRepository genreRepository;

    public GenreController(LiteraryGenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @GetMapping
    public List<Map<String, ?>> getGenres() {
        return genreRepository.findAll().stream()
                .map(g -> Map.of("id", g.getId(), "nom", g.getNomGenre()))
                .collect(Collectors.toList());
    }
}
