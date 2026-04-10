package com.damc.legalnotices.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@Entity
@Table(name = "send_error_sms_details")
public class SendErrorSmsDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sno")
    private Long id;

    @Column(name = "type", length = 45)
    private String type;

    @Column(name = "schedule_sno")
    private Long scheduleSno;

    @Column(name = "process_sno")
    private Long processSno;

    @Column(name = "sms_template_sno")
    private Long smsTemplateSno;

    @Column(name = "loan_no")
    private Long loanNo;

    @Column(name = "send_to", length = 15)
    private String sendTo;

    @Column(name = "message", columnDefinition = "longtext")
    private String message;

    @Column(name = "send_at")
    private Instant sendAt;

    @Column(name = "error_message", columnDefinition = "longtext")
    private String errorMessage;

    @Column(name = "created_by")
    private Long createdBy;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;
}
