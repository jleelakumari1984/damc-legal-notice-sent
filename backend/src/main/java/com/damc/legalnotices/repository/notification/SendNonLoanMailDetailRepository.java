package com.damc.legalnotices.repository.notification;

import com.damc.legalnotices.entity.notification.SendNonLoanMailDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SendNonLoanMailDetailRepository extends JpaRepository<SendNonLoanMailDetailEntity, Long> {
}
