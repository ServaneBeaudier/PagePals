package com.pagepals.circle.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pagepals.circle.model.Circle;

public interface CircleRepository extends JpaRepository <Circle, Long>{

    boolean existsByCreateurIdAndDateRencontre(Long createurId, LocalDateTime dateRencontre);
    boolean existsByCreateurIdAndDateRencontreAndIdNot(Long createurId, LocalDateTime dateRencontre, Long id);

}
