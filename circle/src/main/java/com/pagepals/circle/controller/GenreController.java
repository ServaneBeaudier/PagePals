package com.pagepals.circle.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pagepals.circle.repository.LiteraryGenreRepository;

/**
 * Contrôleur REST permettant de récupérer la liste des genres littéraires disponibles.
 * 
 * Utilisé lors de la création ou de la recherche de cercles pour proposer les choix de genres.
 */
@RestController
@RequestMapping("/api/genres")
public class GenreController {

    private final LiteraryGenreRepository genreRepository;

    /**
     * Constructeur injectant le dépôt de genres littéraires.
     *
     * @param genreRepository repository permettant d'accéder aux genres enregistrés
     */
    public GenreController(LiteraryGenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    /**
     * Retourne la liste de tous les genres littéraires sous forme de paires id/nom.
     *
     * @return liste des genres disponibles avec leur identifiant et leur nom
     */
    @GetMapping
    public List<Map<String, ?>> getGenres() {
        return genreRepository.findAll().stream()
                .map(g -> Map.of("id", g.getId(), "nom", g.getNomGenre()))
                .collect(Collectors.toList());
    }
}
