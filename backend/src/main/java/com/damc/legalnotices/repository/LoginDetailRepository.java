package com.damc.legalnotices.repository;

import com.damc.legalnotices.entity.LoginDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginDetailRepository extends JpaRepository<LoginDetail, Long> {
    Optional<LoginDetail> findByLoginNameAndEnabledTrue(String loginName);
}
