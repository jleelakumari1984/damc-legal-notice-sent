package com.damc.legalnotices.repository.notification;

import com.damc.legalnotices.entity.notification.SendErrorSmsDetailEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SendErrorSmsDetailRepository extends JpaRepository<SendErrorSmsDetailEntity, Long> {

    @Query("SELECT s FROM SendErrorSmsDetailEntity s left join fetch s.process  left join fetch s.smsConfig WHERE s.scheduleSno = :noticeId")
    Page<SendErrorSmsDetailEntity> findByScheduleSno(Long noticeId, Pageable pageable);
}
