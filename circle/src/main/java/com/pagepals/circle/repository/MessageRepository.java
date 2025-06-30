package com.pagepals.circle.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pagepals.circle.model.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByCircleIdOrderByDateEnvoiAsc(Long circleId);
}
