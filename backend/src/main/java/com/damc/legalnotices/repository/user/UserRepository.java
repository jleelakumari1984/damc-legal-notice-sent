package com.damc.legalnotices.repository.user;

import com.damc.legalnotices.entity.user.UserEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query("SELECT DISTINCT u FROM UserEntity u LEFT JOIN FETCH u.credits WHERE u.loginName = :loginName AND u.enabled = true")
    Optional<UserEntity> findByLoginNameAndEnabledTrue(String loginName);

    @Query("SELECT DISTINCT u FROM UserEntity u LEFT JOIN FETCH u.credits WHERE u.loginName = :loginName")
    Optional<UserEntity> findByLoginName(String loginName);

    boolean existsByLoginName(String loginName);

    boolean existsByLoginNameAndIdNot(String loginName, Long id);

    boolean existsByUserEmail(String userEmail);

    @Query("SELECT DISTINCT u FROM UserEntity u LEFT JOIN FETCH u.credits")
    Page<UserEntity> findAllWithCredits(Pageable pagination);

    @Query("SELECT DISTINCT u FROM UserEntity u LEFT JOIN FETCH u.credits " +
            "WHERE (:search IS NULL OR LOWER(u.displayName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(u.loginName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(u.userMobileSms) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(u.userMobileWhatsapp) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(u.userEmail) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:accessLevel IS NULL OR u.accessLevel = :accessLevel) " +
            "AND (:enabled IS NULL OR u.enabled = :enabled)")
    Page<UserEntity> findAllWithCreditsFiltered(
            @Param("search") String search,
            @Param("accessLevel") Integer accessLevel,
            @Param("enabled") Boolean enabled,
            Pageable pagination);

    @Query("SELECT DISTINCT u FROM UserEntity u LEFT JOIN FETCH u.credits WHERE u.id = :id")
    Optional<UserEntity> findByIdWithCredit(@Param("id") Long id);
}
