package com.damc.legalnotices.repository.user;

import com.damc.legalnotices.entity.user.LoginDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginDetailRepository extends JpaRepository<LoginDetailEntity, Long> {
    Optional<LoginDetailEntity> findByLoginNameAndEnabledTrue(String loginName);
    Optional<LoginDetailEntity> findByLoginName(String loginName);
    boolean existsByLoginName(String loginName);
    boolean existsByUserEmail(String userEmail);
}
