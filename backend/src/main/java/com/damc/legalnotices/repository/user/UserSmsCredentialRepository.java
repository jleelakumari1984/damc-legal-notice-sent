package com.damc.legalnotices.repository.user;

import com.damc.legalnotices.entity.user.UserSmsCredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSmsCredentialRepository extends JpaRepository<UserSmsCredentialEntity, Long> {
    Optional<UserSmsCredentialEntity> findByUserId(Long userId);
}
