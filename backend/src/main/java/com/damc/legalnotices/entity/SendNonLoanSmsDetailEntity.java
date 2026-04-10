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

@Getter
@Setter
@Entity
@Table(name = "send_non_loan_sms_details")
public class SendNonLoanSmsDetailEntity {

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

  @Column(name = "send_to", length = 15)
  private String sendTo;

  @Column(name = "message", columnDefinition = "longtext")
  private String message;

  @Column(name = "send_at")
  private Instant sendAt;

  @Column(name = "send_status")
  private Integer sendStatus;

  @Column(name = "send_response", columnDefinition = "longtext")
  private String sendResponse;

  @Column(name = "ack_id")
  private String ackId;

  @Column(name = "created_by")
  private Long createdBy;

  @Column(name = "created_at", insertable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "error_message", columnDefinition = "longtext")
  private String errorMessage;
}
