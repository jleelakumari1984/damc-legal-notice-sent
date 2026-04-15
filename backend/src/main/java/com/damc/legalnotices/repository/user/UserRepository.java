package com.damc.legalnotices.repository.user;

import com.damc.legalnotices.entity.user.UserEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByLoginNameAndEnabledTrue(String loginName);

    Optional<UserEntity> findByLoginName(String loginName);

    boolean existsByLoginName(String loginName);

    boolean existsByUserEmail(String userEmail);

    @Query("SELECT DISTINCT u FROM UserEntity u LEFT JOIN FETCH u.credits")
    Page<UserEntity> findAllWithCredits(Pageable pagination);

    @Query("SELECT DISTINCT u FROM UserEntity u LEFT JOIN FETCH u.credits WHERE u.id = :id")
    Optional<UserEntity> findByIdWithCredit(Long id);
}
