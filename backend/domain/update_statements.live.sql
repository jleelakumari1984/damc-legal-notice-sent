-- =============================================================
-- update_statements.live.sql
-- Diff: live production schema (schema_live.sql) → entity state
-- Database: whatapp_sms_notices  (DigitalOcean)
-- Run order: STEP 1 → STEP 2 → STEP 3 → STEP 4
-- =============================================================

-- =============================================================
-- STEP 1 — Rename tables
-- =============================================================
-- drop table master_loan_document_types;master_process_doc_config_details;  -- not present in entity schema at all, so dropped from live
ALTER TABLE `whatapp_sms_notices_staging`.`master_process_mail_config_details` 
DROP FOREIGN KEY `FKewtg0n3ys7r0o22jmum9isawv`;
ALTER TABLE `whatapp_sms_notices_staging`.`master_process_mail_config_details` 
DROP COLUMN `sent_level`,
DROP COLUMN `hearing_stage_sno`,
DROP INDEX `FKewtg0n3ys7r0o22jmum9isawv` ;
;
ALTER TABLE `whatapp_sms_notices_staging`.`master_process_sms_config_details` 
DROP FOREIGN KEY `FK35p5n4folv81gdepft1lf9xnh`;
ALTER TABLE `whatapp_sms_notices_staging`.`master_process_sms_config_details` 
DROP COLUMN `sent_level`,
DROP COLUMN `hearing_stage_sno`,
DROP INDEX `FK35p5n4folv81gdepft1lf9xnh` ;
;
ALTER TABLE `whatapp_sms_notices_staging`.`master_process_whatsapp_config_details` 
DROP FOREIGN KEY `FKad1kjekj2b4xkw1iyqf7802cf`;
ALTER TABLE `whatapp_sms_notices_staging`.`master_process_whatsapp_config_details` 
DROP COLUMN `sent_level`,
DROP COLUMN `hearing_stage_sno`,
DROP INDEX `FKad1kjekj2b4xkw1iyqf7802cf` ;
;
-- drop table master_hearing_stages;
-- -------------------------------------------------------------
-- login_details → users  (UserEntity @Table(name="users"))
-- -------------------------------------------------------------
RENAME TABLE `login_details` TO `users`;

ALTER TABLE  `users` 
CHANGE COLUMN `sno` `sno` BIGINT NOT NULL AUTO_INCREMENT ;

-- -------------------------------------------------------------
-- process_excel_mapping → master_process_excel_mapping
-- (MasterProcessExcelMappingEntity @Table(name="master_process_excel_mapping"))
-- -------------------------------------------------------------
RENAME TABLE `process_excel_mapping` TO `master_process_excel_mapping`;


-- =============================================================
-- STEP 2 — Alter existing tables (add missing columns)
-- =============================================================

-- -------------------------------------------------------------
-- users  (was login_details)
-- Entity adds: created_at, updated_at
-- -------------------------------------------------------------
ALTER TABLE `users`
  ADD COLUMN `created_at`  datetime(6)  DEFAULT NULL,
  ADD COLUMN `updated_at`  datetime(6)  DEFAULT NULL;

-- -------------------------------------------------------------
-- master_process_template_details
-- Entity adds: created_by, updated_by, updated_at
-- -------------------------------------------------------------
ALTER TABLE `master_process_template_details`
  ADD COLUMN `created_by`  bigint       DEFAULT NULL,
  ADD COLUMN `updated_by`  bigint       DEFAULT NULL,
  ADD COLUMN `updated_at`  datetime(6)  DEFAULT NULL;

-- -------------------------------------------------------------
-- master_process_mail_config_details
-- Entity adds: created_by, updated_by, updated_at
-- -------------------------------------------------------------
ALTER TABLE `master_process_mail_config_details`
  ADD COLUMN `created_by`  bigint       DEFAULT NULL,
  ADD COLUMN `updated_by`  bigint       DEFAULT NULL,
  ADD COLUMN `updated_at`  datetime(6)  DEFAULT NULL;

-- -------------------------------------------------------------
-- master_process_sms_config_details
-- Entity adds: user_template_path, created_by, updated_by, updated_at,
--              approve_status, approved_by, approved_at
-- -------------------------------------------------------------
ALTER TABLE `master_process_sms_config_details`
  ADD COLUMN `user_template_path`  varchar(255)  DEFAULT NULL,
  ADD COLUMN `created_by`          bigint        DEFAULT NULL,
  ADD COLUMN `updated_by`          bigint        DEFAULT NULL,
  ADD COLUMN `updated_at`          datetime(6)   DEFAULT NULL,
  ADD COLUMN `approve_status`      int           DEFAULT NULL,
  ADD COLUMN `approved_by`         bigint        DEFAULT NULL,
  ADD COLUMN `approved_at`         datetime(6)   DEFAULT NULL;

-- -------------------------------------------------------------
-- master_process_whatsapp_config_details
-- Entity adds: user_template_path, created_by, updated_by, updated_at,
--              approve_status, approved_by, approved_at
-- -------------------------------------------------------------
ALTER TABLE `master_process_whatsapp_config_details`
  ADD COLUMN `user_template_path`  varchar(255)  DEFAULT NULL,
  ADD COLUMN `created_by`          bigint        DEFAULT NULL,
  ADD COLUMN `updated_by`          bigint        DEFAULT NULL,
  ADD COLUMN `updated_at`          datetime(6)   DEFAULT NULL,
  ADD COLUMN `approve_status`      int           DEFAULT NULL,
  ADD COLUMN `approved_by`         bigint        DEFAULT NULL,
  ADD COLUMN `approved_at`         datetime(6)   DEFAULT NULL;

-- -------------------------------------------------------------
-- master_process_excel_mapping  (was process_excel_mapping)
-- Entity adds: created_by, updated_by, updated_at
-- -------------------------------------------------------------
ALTER TABLE `master_process_excel_mapping`
  ADD COLUMN `created_by`  bigint       DEFAULT NULL,
  ADD COLUMN `updated_by`  bigint       DEFAULT NULL,
  ADD COLUMN `updated_at`  datetime(6)  DEFAULT NULL;

-- -------------------------------------------------------------
-- scheduled_notices
-- Entity adds: updated_by, updated_at
-- -------------------------------------------------------------
ALTER TABLE `scheduled_notices`
  ADD COLUMN `updated_by`  bigint       DEFAULT NULL,
  ADD COLUMN `updated_at`  datetime(6)  DEFAULT NULL;

-- -------------------------------------------------------------
-- send_error_mail_details
-- Entity adds: updated_by, updated_at
-- (live already has: created_date + created_at — both kept)
-- -------------------------------------------------------------
ALTER TABLE `send_error_mail_details`
  ADD COLUMN `updated_by`  bigint       DEFAULT NULL,
  ADD COLUMN `updated_at`  datetime(6)  DEFAULT NULL;

-- -------------------------------------------------------------
-- send_error_sms_details
-- Entity adds: updated_by, updated_at
-- -------------------------------------------------------------
ALTER TABLE `send_error_sms_details`
  ADD COLUMN `updated_by`  bigint       DEFAULT NULL,
  ADD COLUMN `updated_at`  datetime(6)  DEFAULT NULL;

-- -------------------------------------------------------------
-- send_loan_mail_details
-- Entity adds: updated_by, updated_at
-- -------------------------------------------------------------
ALTER TABLE `send_loan_mail_details`
  ADD COLUMN `updated_by`  bigint       DEFAULT NULL,
  ADD COLUMN `updated_at`  datetime(6)  DEFAULT NULL;

-- -------------------------------------------------------------
-- send_loan_sms_details
-- Entity adds: updated_by, updated_at
-- (live already has ack_id, received_status, received_at, error_message)
-- -------------------------------------------------------------
ALTER TABLE `send_loan_sms_details`
  ADD COLUMN `updated_by`  bigint       DEFAULT NULL,
  ADD COLUMN `updated_at`  datetime(6)  DEFAULT NULL;

-- -------------------------------------------------------------
-- send_loan_whatsapp_details
-- Entity adds: updated_by, updated_at
-- -------------------------------------------------------------
ALTER TABLE `send_loan_whatsapp_details`
  ADD COLUMN `updated_by`  bigint       DEFAULT NULL,
  ADD COLUMN `updated_at`  datetime(6)  DEFAULT NULL;

-- -------------------------------------------------------------
-- send_non_loan_whatsapp_details
-- Entity adds: updated_by, updated_at
-- -------------------------------------------------------------
ALTER TABLE `send_non_loan_whatsapp_details`
  ADD COLUMN `updated_by`  bigint       DEFAULT NULL,
  ADD COLUMN `updated_at`  datetime(6)  DEFAULT NULL;


-- =============================================================
-- STEP 3 — Create missing tables
-- =============================================================

-- -------------------------------------------------------------
-- send_non_loan_sms_details  (SendNonLoanSmsDetailEntity)
-- Not present in live schema at all
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `send_non_loan_sms_details` (
  `sno`               bigint        NOT NULL AUTO_INCREMENT,
  `type`              varchar(45)   DEFAULT NULL,
  `schedule_sno`      bigint        DEFAULT NULL,
  `process_sno`       bigint        DEFAULT NULL,
  `sms_template_sno`  bigint        DEFAULT NULL,
  `send_to`           varchar(15)   DEFAULT NULL,
  `message`           longtext,
  `send_at`           timestamp     NULL DEFAULT NULL,
  `send_status`       int           DEFAULT NULL,
  `send_response`     longtext,
  `ack_id`            varchar(150)  DEFAULT NULL,
  `received_status`   varchar(50)   DEFAULT NULL,
  `received_at`       timestamp     NULL DEFAULT NULL,
  `error_message`     longtext,
  `created_by`        bigint        DEFAULT NULL,
  `created_at`        datetime(6)   DEFAULT NULL,
  `updated_by`        bigint        DEFAULT NULL,
  `updated_at`        datetime(6)   DEFAULT NULL,
  PRIMARY KEY (`sno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- -------------------------------------------------------------
-- send_non_loan_mail_details  (SendNonLoanMailDetailEntity)
-- Not present in live schema at all
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `send_non_loan_mail_details` (
  `sno`               bigint        NOT NULL AUTO_INCREMENT,
  `type`              varchar(45)   DEFAULT NULL,
  `schedule_sno`      bigint        DEFAULT NULL,
  `process_sno`       bigint        DEFAULT NULL,
  `mail_template_sno` bigint        DEFAULT NULL,
  `send_to`           longtext,
  `send_cc`           longtext,
  `subject`           varchar(300)  DEFAULT NULL,
  `message`           longtext,
  `send_at`           timestamp     NULL DEFAULT NULL,
  `send_status`       int           DEFAULT NULL,
  `send_response`     tinytext,
  `created_by`        bigint        DEFAULT NULL,
  `created_at`        datetime(6)   DEFAULT NULL,
  `updated_by`        bigint        DEFAULT NULL,
  `updated_at`        datetime(6)   DEFAULT NULL,
  PRIMARY KEY (`sno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- -------------------------------------------------------------
-- user_credits  (UserCreditEntity)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_credits` (
  `id`               bigint       NOT NULL AUTO_INCREMENT,
  `user_id`          bigint       NOT NULL,
  `sms_credits`      bigint       NOT NULL DEFAULT 0,
  `whatsapp_credits` bigint       NOT NULL DEFAULT 0,
  `mail_credits`     bigint       NOT NULL DEFAULT 0,
  `created_by`       bigint       DEFAULT NULL,
  `created_at`       datetime(6)  DEFAULT NULL,
  `updated_by`       bigint       DEFAULT NULL,
  `updated_at`       datetime(6)  DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_credits_user` (`user_id`),
  CONSTRAINT `fk_user_credits_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`sno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- -------------------------------------------------------------
-- user_credit_transactions  (UserCreditTransactionEntity)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_credit_transactions` (
  `id`             bigint          NOT NULL AUTO_INCREMENT,
  `user_id`        bigint          NOT NULL,
  `channel`        varchar(20)     NOT NULL,
  `credits`        bigint          NOT NULL,
  `price_per_unit` decimal(10,4)   DEFAULT NULL,
  `type`           varchar(10)     NOT NULL,
  `description`    varchar(500)    DEFAULT NULL,
  `status`         varchar(20)     NOT NULL DEFAULT 'COMPLETED',
  `created_by`     bigint          NOT NULL,
  `created_at`     datetime(6)     NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_uct_user` (`user_id`),
  CONSTRAINT `fk_user_credit_transactions_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`sno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- -------------------------------------------------------------
-- user_activity_logs  (UserActivityLogEntity)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_activity_logs` (
  `id`            bigint        NOT NULL AUTO_INCREMENT,
  `user_id`       bigint        NOT NULL,
  `performed_by`  bigint        NOT NULL,
  `action`        varchar(30)   NOT NULL,
  `description`   varchar(1000) DEFAULT NULL,
  `created_at`    datetime(6)   DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_ual_user` (`user_id`),
  KEY `idx_ual_performed_by` (`performed_by`),
  CONSTRAINT `fk_activity_logs_user`         FOREIGN KEY (`user_id`)      REFERENCES `users` (`sno`),
  CONSTRAINT `fk_activity_logs_performed_by` FOREIGN KEY (`performed_by`) REFERENCES `users` (`sno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- -------------------------------------------------------------
-- user_sms_credentials  (credentials stored in JSON files / EndpointUtil)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_sms_credentials` (
  `id`                  bigint       NOT NULL AUTO_INCREMENT,
  `user_id`             bigint       NOT NULL,
  `url`                 varchar(500) NOT NULL,
  `user_name`           varchar(255) NOT NULL,
  `password`            varchar(255) NOT NULL,
  `is_live`             tinyint(1)   NOT NULL DEFAULT 0,
  `test_mobile_number`  varchar(20)  DEFAULT NULL,
  `created_by`          bigint       DEFAULT NULL,
  `created_at`          datetime(6)  DEFAULT NULL,
  `updated_by`          bigint       DEFAULT NULL,
  `updated_at`          datetime(6)  DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_sms_credentials_user` (`user_id`),
  CONSTRAINT `fk_user_sms_credentials_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`sno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- -------------------------------------------------------------
-- user_whatsapp_credentials  (credentials stored in JSON files / EndpointUtil)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_whatsapp_credentials` (
  `id`                       bigint       NOT NULL AUTO_INCREMENT,
  `user_id`                  bigint       NOT NULL,
  `url`                      varchar(500) NOT NULL,
  `access_token`             varchar(500) NOT NULL,
  `attachment_download_url`  varchar(500) DEFAULT NULL,
  `is_live`                  tinyint(1)   NOT NULL DEFAULT 0,
  `test_mobile_number`       varchar(20)  DEFAULT NULL,
  `created_by`               bigint       DEFAULT NULL,
  `created_at`               datetime(6)  DEFAULT NULL,
  `updated_by`               bigint       DEFAULT NULL,
  `updated_at`               datetime(6)  DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_whatsapp_credentials_user` (`user_id`),
  CONSTRAINT `fk_user_whatsapp_credentials_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`sno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- =============================================================
-- STEP 4 — Create / replace views
-- =============================================================

-- -------------------------------------------------------------
-- shedule_report  (ScheduleReportViewEntity — read-only)
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
    mptd.name      AS template_name,
    mptd.step_name AS template_step_name,
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
-- process_config_report  (ProcessConfigReportViewEntity — read-only)
-- Note: created_user_name joined from users.display_name
--       excel_map_count uses renamed table master_process_excel_mapping
-- -------------------------------------------------------------
CREATE OR REPLACE VIEW `process_config_report` AS
  SELECT
    mptd.sno,
    mptd.created_at,
    mptd.created_by,
    u.display_name  AS created_user_name,
    mptd.description,
    mptd.name,
    mptd.step_name,
    em.excel_map_count,
    sms.sms_map_count,
    wapp.whatsapp_map_count,
    mail.mail_map_count
  FROM master_process_template_details mptd
  LEFT JOIN users u ON u.sno = mptd.created_by
  LEFT JOIN (
    SELECT process_id, COUNT(0) AS excel_map_count
    FROM master_process_excel_mapping
    GROUP BY process_id
  ) em   ON mptd.sno = em.process_id
  LEFT JOIN (
    SELECT process_sno, COUNT(0) AS sms_map_count
    FROM master_process_sms_config_details
    GROUP BY process_sno
  ) sms  ON mptd.sno = sms.process_sno
  LEFT JOIN (
    SELECT process_sno, COUNT(0) AS whatsapp_map_count
    FROM master_process_whatsapp_config_details
    GROUP BY process_sno
  ) wapp ON mptd.sno = wapp.process_sno
  LEFT JOIN (
    SELECT process_sno, COUNT(0) AS mail_map_count
    FROM master_process_mail_config_details
    GROUP BY process_sno
  ) mail ON mptd.sno = mail.process_sno;
