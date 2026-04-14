package com.damc.legalnotices.repository.user;

import com.damc.legalnotices.entity.user.UserCreditEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCreditRepository extends JpaRepository<UserCreditEntity, Long> {
    Optional<UserCreditEntity> findByUserId(Long userId);
}
