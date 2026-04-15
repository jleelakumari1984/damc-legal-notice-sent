package com.damc.legalnotices.repository.user;

import com.damc.legalnotices.entity.user.UserWhatsAppCredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserWhatsAppCredentialRepository extends JpaRepository<UserWhatsAppCredentialEntity, Long> {
    Optional<UserWhatsAppCredentialEntity> findByUserId(Long userId);
}
