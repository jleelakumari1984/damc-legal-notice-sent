package com.damc.legalnotices.repository.notification;

import com.damc.legalnotices.entity.notification.SendErrorSmsDetailEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SendErrorSmsDetailRepository extends JpaRepository<SendErrorSmsDetailEntity, Long> {

    Page<SendErrorSmsDetailEntity> findByScheduleSno(Long noticeId, Pageable pageable);
}
