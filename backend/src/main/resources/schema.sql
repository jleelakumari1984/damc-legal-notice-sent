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

CREATE TABLE IF NOT EXISTS scheduled_notice_items (
  id BIGINT NOT NULL AUTO_INCREMENT,
  scheduled_notice_id BIGINT NOT NULL,
  agreement_number VARCHAR(100) NOT NULL,
  customer_name VARCHAR(200) NULL,
  mobile_sms VARCHAR(20) NULL,
  mobile_whatsapp VARCHAR(20) NULL,
  pdf_file_name VARCHAR(255) NULL,
  pdf_file_path VARCHAR(500) NULL,
  document_present BIT(1) NOT NULL,
  status VARCHAR(20) NOT NULL,
  failure_reason VARCHAR(500) NULL,
  processed_at TIMESTAMP NULL,
  PRIMARY KEY (id),
  INDEX idx_notice_item_notice (scheduled_notice_id),
  INDEX idx_notice_item_status (status),
  CONSTRAINT fk_notice_item_notice
    FOREIGN KEY (scheduled_notice_id) REFERENCES scheduled_notices (id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
