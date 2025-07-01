package com.pagepals.membership.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pagepals.membership.model.Membership;

public interface MembershipRepository extends JpaRepository<Membership, Long>{

    Optional<Membership> findByUserIdAndCircleId(long userId, long circleId);
    List<Membership> findByCircleId(long circleId);
    boolean existsByUserIdAndCircleId(long userId, long circleId);
    void deleteByUserIdAndCircleId(long userId, long circleId);
    int countByCircleId(long circleId);
    List<Membership> findByUserId(Long userId);
}
