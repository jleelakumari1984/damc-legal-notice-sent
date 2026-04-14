package com.damc.legalnotices.entity.view;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

@Getter
@Entity
@Immutable
@Table(name = "shedule_report")
public class ScheduleReportViewEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "extracted_folder_path")
    private String extractedFolderPath;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "identifier")
    private Long identifier;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "process_sno")
    private Long processSno;

    @Column(name = "template_name")
    private String templateName;

    @Column(name = "template_step_name")
    private String templateStepName;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "send_sms")
    private Boolean sendSms;

    @Column(name = "send_whatsapp")
    private Boolean sendWhatsapp;

    @Column(name = "status")
    private String status;

    @Column(name = "zip_file_path")
    private String zipFilePath;

    @Column(name = "total_loans")
    private Long totalLoans;

    @Column(name = "pending_loans")
    private Long pendingLoans;

    @Column(name = "processing_loans")
    private Long processingLoans;

    @Column(name = "completed_loans")
    private Long completedLoans;

    @Column(name = "failed_loans")
    private Long failedLoans;
}
