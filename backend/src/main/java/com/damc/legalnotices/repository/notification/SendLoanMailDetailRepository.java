package com.damc.legalnotices.repository.notification;

import com.damc.legalnotices.entity.notification.SendLoanMailDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SendLoanMailDetailRepository extends JpaRepository<SendLoanMailDetailEntity, Long> {
}
