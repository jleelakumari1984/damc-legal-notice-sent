package com.damc.legalnotices.repository;

import com.damc.legalnotices.entity.LoginDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginDetailRepository extends JpaRepository<LoginDetailEntity, Long> {
    Optional<LoginDetailEntity> findByLoginNameAndEnabledTrue(String loginName);
}
