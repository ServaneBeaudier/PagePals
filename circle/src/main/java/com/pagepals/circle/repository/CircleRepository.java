package com.pagepals.circle.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pagepals.circle.model.Circle;

public interface CircleRepository extends JpaRepository <Circle, Long>{

}
