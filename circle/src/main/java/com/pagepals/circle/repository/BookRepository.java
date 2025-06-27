
package com.pagepals.circle.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pagepals.circle.model.Book;

public interface BookRepository extends JpaRepository<Book, Long>{

}
