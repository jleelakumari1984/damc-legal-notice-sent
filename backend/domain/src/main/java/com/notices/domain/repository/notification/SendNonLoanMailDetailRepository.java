package com.notices.domain.repository.notification;

import com.notices.domain.entity.notification.SendNonLoanMailDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SendNonLoanMailDetailRepository extends JpaRepository<SendNonLoanMailDetailEntity, Long> {
}
