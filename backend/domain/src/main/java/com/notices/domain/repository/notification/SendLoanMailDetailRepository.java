package com.notices.domain.repository.notification;

import com.notices.domain.entity.notification.SendLoanMailDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SendLoanMailDetailRepository extends JpaRepository<SendLoanMailDetailEntity, Long> {
}
