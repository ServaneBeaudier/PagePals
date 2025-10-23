package com.pagepals.circle.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pagepals.circle.model.LiteraryGenre;

/**
 * Repository JPA pour la gestion des entités {@link LiteraryGenre}.
 * 
 * Permet d'accéder aux genres littéraires enregistrés et de rechercher
 * un genre à partir de son nom, sans distinction de casse.
 */
public interface LiteraryGenreRepository extends JpaRepository<LiteraryGenre, Long> {

    /**
     * Recherche un genre littéraire à partir de son nom, sans tenir compte de la casse.
     *
     * @param nom nom du genre recherché
     * @return un Optional contenant le genre s'il existe, sinon vide
     */
    Optional<LiteraryGenre> findByNomGenreIgnoreCase(String nom);
}
