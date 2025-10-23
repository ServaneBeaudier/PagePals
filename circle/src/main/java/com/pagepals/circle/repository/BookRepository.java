
package com.pagepals.circle.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pagepals.circle.model.Book;


/**
 * Repository JPA pour la gestion des entités {@link Book}.
 * 
 * Fournit les opérations CRUD de base pour les livres associés aux cercles littéraires.
 */
public interface BookRepository extends JpaRepository<Book, Long>{

}
