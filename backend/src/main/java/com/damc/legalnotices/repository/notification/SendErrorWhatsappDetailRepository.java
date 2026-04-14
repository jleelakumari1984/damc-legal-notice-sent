package com.damc.legalnotices.repository.notification;

import com.damc.legalnotices.entity.notification.SendErrorWhatsappDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SendErrorWhatsappDetailRepository extends JpaRepository<SendErrorWhatsappDetailEntity, Long> {
}
