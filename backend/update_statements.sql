-- =============================================================
-- Schema Update Statements
-- Generated: 2026-04-11
-- Compared: schema.sql (existing DB) vs all JPA entities
-- =============================================================

-- All existing tables match their entities.
-- The following 2 tables are defined in entities but missing from the DB.

-- -------------------------------------------------------------
-- user_credits  (UserCreditEntity)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_credits` (
  `id`               bigint NOT NULL AUTO_INCREMENT,
  `user_id`          bigint NOT NULL,
  `sms_credits`      bigint NOT NULL DEFAULT '0',
  `whatsapp_credits` bigint NOT NULL DEFAULT '0',
  `mail_credits`     bigint NOT NULL DEFAULT '0',
  `updated_at`       datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_user_credits_user` FOREIGN KEY (`user_id`) REFERENCES `login_details` (`sno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- -------------------------------------------------------------
-- credit_transactions  (CreditTransactionEntity)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `credit_transactions` (
  `id`             bigint NOT NULL AUTO_INCREMENT,
  `user_id`        bigint NOT NULL,
  `channel`        varchar(20) NOT NULL,
  `credits`        bigint NOT NULL,
  `price_per_unit` decimal(10,4) DEFAULT NULL,
  `type`           varchar(10) NOT NULL,
  `description`    varchar(500) DEFAULT NULL,
  `status`         varchar(20) NOT NULL DEFAULT 'COMPLETED',
  `created_by`     bigint NOT NULL,
  `created_at`     datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_credit_transactions_user` FOREIGN KEY (`user_id`) REFERENCES `login_details` (`sno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- -------------------------------------------------------------
-- shedule_report  (ScheduleReportEntity — read-only VIEW)
-- -------------------------------------------------------------
CREATE OR REPLACE VIEW `shedule_report` AS
  SELECT
    sne.id,
    sne.created_at,
    sne.created_by,
    sne.extracted_folder_path,
    sne.failure_reason,
    sne.identifier,
    sne.original_file_name,
    sne.process_sno,
    mptd.name           AS template_name,
    mptd.step_name      AS template_step_name,
    sne.processed_at,
    sne.send_sms,
    sne.send_whatsapp,
    sne.status,
    sne.zip_file_path,
    snt.total_loans,
    snt.pending_loans,
    snt.processing_loans,
    snt.completed_loans,
    snt.failed_loans
  FROM scheduled_notices sne
  JOIN master_process_template_details mptd ON mptd.sno = sne.process_sno
  LEFT JOIN (
    SELECT
      scheduled_notice_id,
      COUNT(id)                              AS total_loans,
      SUM(IF(status = 'PENDING',     1, 0))  AS pending_loans,
      SUM(IF(status = 'PROCESSING',  1, 0))  AS processing_loans,
      SUM(IF(status = 'COMPLETED',   1, 0))  AS completed_loans,
      SUM(IF(status = 'FAILED',      1, 0))  AS failed_loans
    FROM scheduled_notice_items
    GROUP BY scheduled_notice_id
  ) snt ON snt.scheduled_notice_id = sne.id;

-- -------------------------------------------------------------
-- process_config_report  (ProcessConfigReportEntity — read-only VIEW)
-- -------------------------------------------------------------
CREATE OR REPLACE VIEW `process_config_report` AS
  SELECT
    mptde1_0.sno,
    mptde1_0.created_at,
    mptde1_0.description,
    mptde1_0.name,
    mptde1_0.step_name,
    em1_0.excel_map_count,
    sms1_0.sms_map_count,
    wapp_0.wahtaapp_map_count,
    mail_0.mail_map_count
  FROM master_process_template_details mptde1_0
  LEFT JOIN (
    SELECT process_id, COUNT(0) AS excel_map_count
    FROM process_excel_mapping
    GROUP BY process_id
  ) em1_0 ON mptde1_0.sno = em1_0.process_id
  LEFT JOIN (
    SELECT process_sno, COUNT(0) AS sms_map_count
    FROM master_process_sms_config_details
    GROUP BY process_sno
  ) sms1_0 ON mptde1_0.sno = sms1_0.process_sno
  LEFT JOIN (
    SELECT process_sno, COUNT(0) AS wahtaapp_map_count
    FROM master_process_whatsapp_config_details
    GROUP BY process_sno
  ) wapp_0 ON mptde1_0.sno = wapp_0.process_sno
  LEFT JOIN (
    SELECT process_sno, COUNT(0) AS mail_map_count
    FROM master_process_mail_config_details
    GROUP BY process_sno
  ) mail_0 ON mptde1_0.sno = mail_0.process_sno;
