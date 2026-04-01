package com.damc.legalnotices.entity;

import com.damc.legalnotices.enums.ProcessingStatus;
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
public class ScheduledNoticeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheduled_notice_id", nullable = false)
    private ScheduledNotice scheduledNotice;

    @Column(name = "agreement_number", nullable = false)
    private String agreementNumber;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "mobile_sms")
    private String mobileSms;

    @Column(name = "mobile_whatsapp")
    private String mobileWhatsapp;

    @Column(name = "pdf_file_name")
    private String pdfFileName;

    @Column(name = "pdf_file_path")
    private String pdfFilePath;

    @Column(name = "document_present", nullable = false)
    private Boolean documentPresent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProcessingStatus status = ProcessingStatus.PENDING;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;
}
