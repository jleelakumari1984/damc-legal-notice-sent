package com.notices.domain.repository.notification;

import com.notices.domain.entity.notification.SendErrorMailDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SendErrorMailDetailRepository extends JpaRepository<SendErrorMailDetailEntity, Long> {
}
