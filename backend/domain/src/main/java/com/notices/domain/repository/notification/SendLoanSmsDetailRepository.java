package com.notices.domain.repository.notification;

import com.notices.domain.entity.notification.SendLoanSmsDetailEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SendLoanSmsDetailRepository extends JpaRepository<SendLoanSmsDetailEntity, Long> {
    Optional<SendLoanSmsDetailEntity> findByAckId(String ackId);

    List<SendLoanSmsDetailEntity> findAllByAckIdIsNullAndSendResponseIsNotNull();

    @Query("SELECT s FROM SendLoanSmsDetailEntity s left join fetch s.process  left join fetch s.smsConfig WHERE s.scheduleSno = :noticeId AND s.sendResponse IS NOT NULL AND s.receivedStatus = :receivedStatus")
    Page<SendLoanSmsDetailEntity> findByScheduleSnoAndReceivedStatus(Long noticeId, String receivedStatus,
            Pageable pageable);

    @Query("SELECT s FROM SendLoanSmsDetailEntity s left join fetch s.process  left join fetch s.smsConfig WHERE s.scheduleSno = :noticeId")
    Page<SendLoanSmsDetailEntity> findByScheduleSno(Long noticeId, Pageable pageable);
}
