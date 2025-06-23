package com.pagepals.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pagepals.user.model.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

}
