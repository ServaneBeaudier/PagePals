package com.pagepals.circle.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pagepals.circle.model.Circle;

public interface CircleRepository extends JpaRepository<Circle, Long> {

    boolean existsByCreateurIdAndDateRencontre(Long createurId, LocalDateTime dateRencontre);

    boolean existsByCreateurIdAndDateRencontreAndIdNot(Long createurId, LocalDateTime dateRencontre, Long id);

    List<Circle> findByDateRencontreBeforeAndIsArchivedFalse(LocalDate date);

    List<Circle> findByIsArchivedFalse();

    List<Circle> findByIsArchivedTrue();

}
