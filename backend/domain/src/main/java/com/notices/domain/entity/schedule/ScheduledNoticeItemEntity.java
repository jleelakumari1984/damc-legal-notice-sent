package com.notices.domain.entity.schedule;

import com.notices.domain.enums.NoticeScheduleStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "scheduled_notice_items")
public class ScheduledNoticeItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheduled_notice_id", nullable = false)
    private ScheduledNoticeEntity scheduledNotice;

    @Column(name = "agreement_number", nullable = false, length = 255)
    private String agreementNumber;

    @Column(name = "customer_name", nullable = false, length = 255)
    private String customerName;

    @Column(name = "excel_data", columnDefinition = "longtext")
    private String excelData;

    @Column(name = "identifier", nullable = false)
    private Long identifier;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NoticeScheduleStatus status = NoticeScheduleStatus.PENDING;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "attachments")
    private String attachments;
}
