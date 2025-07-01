package com.pagepals.circle.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pagepals.circle.model.LiteraryGenre;

public interface LiteraryGenreRepository extends JpaRepository<LiteraryGenre, Long>{

    Optional<LiteraryGenre> findByNomGenreIgnoreCase(String nom);

}
