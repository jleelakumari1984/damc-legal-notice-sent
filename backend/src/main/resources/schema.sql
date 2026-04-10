CREATE TABLE IF NOT EXISTS scheduled_notices (
  id BIGINT NOT NULL AUTO_INCREMENT,
  process_sno BIGINT NOT NULL,
  zip_file_path VARCHAR(500) NOT NULL,
  extracted_folder_path VARCHAR(500) NOT NULL,
  original_file_name VARCHAR(255) NOT NULL,
  send_sms BIT(1) NOT NULL,
  send_whatsapp BIT(1) NOT NULL,
  status VARCHAR(20) NOT NULL,
  created_by BIGINT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  processed_at TIMESTAMP NULL,
  PRIMARY KEY (id),
  INDEX idx_scheduled_notice_status (status),
  INDEX idx_scheduled_notice_process (process_sno)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `scheduled_notice_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `scheduled_notice_id` bigint NOT NULL,
  `agreement_number` varchar(255) NOT NULL,
  `processed_at` datetime(6) DEFAULT NULL,
  `excel_data` text,
  `status` enum('COMPLETED','FAILED','PENDING','PROCESSING') NOT NULL,
  `failure_reason` text,
  PRIMARY KEY (`id`),
  KEY `FKfa9p95naj6ocinrmic9sc8g8t` (`scheduled_notice_id`),
  CONSTRAINT `FKfa9p95naj6ocinrmic9sc8g8t` FOREIGN KEY (`scheduled_notice_id`) REFERENCES `scheduled_notices` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
