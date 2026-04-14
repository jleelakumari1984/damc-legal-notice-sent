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
