package com.damc.legalnotices.repository.notification;

import com.damc.legalnotices.entity.notification.SendErrorMailDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SendErrorMailDetailRepository extends JpaRepository<SendErrorMailDetailEntity, Long> {
}
