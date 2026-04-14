CREATE TABLE `login_details` (
  `sno` bigint NOT NULL,
  `access_level` bigint DEFAULT NULL,
  `display_name` varchar(255) DEFAULT NULL,
  `enabled` bit(1) DEFAULT NULL,
  `last_login_date` datetime(6) DEFAULT NULL,
  `login_name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `user_code` varchar(255) DEFAULT NULL,
  `user_email` varchar(255) DEFAULT NULL,
  `user_mobile_sms` varchar(255) DEFAULT NULL,
  `user_mobile_whatsapp` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`sno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `master_hearing_stages` (
  `sno` bigint NOT NULL AUTO_INCREMENT,
  `stage_series` varchar(255) DEFAULT NULL,
  `stage_number` int NOT NULL,
  `stage_title` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `enabled` bit(1) DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`sno`),
  UNIQUE KEY `master_UKdrols3dkf0i07h75aci0kgpeb` (`stage_title`),
  UNIQUE KEY `UKdrols3dkf0i07h75aci0kgpeb` (`stage_title`),
  KEY `master_FKthit086oevce1gmvy60gby0df_idx` (`created_by`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `master_loan_document_types` (
  `sno` bigint NOT NULL AUTO_INCREMENT,
  `purpose` varchar(255) DEFAULT NULL,
  `access_level_sno` int DEFAULT NULL,
  `access_level_order` int DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `doc_short_name` varchar(255) DEFAULT NULL,
  `order` int DEFAULT NULL,
  `mandatory` bit(1) DEFAULT b'1',
  `mandatory_second_upload` bit(1) DEFAULT b'0',
  `depend_doc_sno` bigint DEFAULT NULL,
  `wait_days` int DEFAULT NULL,
  `enabled` bit(1) DEFAULT b'1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `has_envelope` bit(1) DEFAULT b'0',
  `has_ack_card` bit(1) DEFAULT b'0',
  PRIMARY KEY (`sno`),
  KEY `master_FKcx2dwl3c1c338438ejeij5oj8` (`depend_doc_sno`),
  KEY `master_FKd8kcbmhruq2c1pm1ofenpvs92` (`access_level_sno`)
) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `master_process_doc_config_details` (
  `sno` bigint NOT NULL AUTO_INCREMENT,
  `doc_sno` bigint NOT NULL,
  `process_sno` bigint NOT NULL,
  `hearing_stage_sno` bigint DEFAULT NULL,
  `template_path` varchar(255) DEFAULT NULL,
  `envelope_path` varchar(255) DEFAULT NULL,
  `post_card_path` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`sno`),
  KEY `master_FKcm1wftcphs9geu4cu8985te29` (`doc_sno`),
  KEY `master_FK6vju53mkfk586r2olv87yp0e7` (`process_sno`),
  KEY `FKkxp8bd99j1jwenrx1hre109b3` (`hearing_stage_sno`),
  CONSTRAINT `FKkxp8bd99j1jwenrx1hre109b3` FOREIGN KEY (`hearing_stage_sno`) REFERENCES `master_hearing_stages` (`sno`),
  CONSTRAINT `master_FK6vju53mkfk586r2olv87yp0e7` FOREIGN KEY (`process_sno`) REFERENCES `master_process_template_details` (`sno`),
  CONSTRAINT `master_FKcm1wftcphs9geu4cu8985te29` FOREIGN KEY (`doc_sno`) REFERENCES `master_loan_document_types` (`sno`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `master_process_mail_config_details` (
  `sno` bigint NOT NULL AUTO_INCREMENT,
  `process_sno` bigint DEFAULT NULL,
  `hearing_stage_sno` bigint DEFAULT NULL,
  `sent_level` int DEFAULT '0',
  `mail_subject` varchar(255) DEFAULT NULL,
  `template_path` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` int DEFAULT '1',
  PRIMARY KEY (`sno`),
  KEY `master_FKedkiusnqr1rie6qfdxes53ooa` (`process_sno`),
  KEY `FKewtg0n3ys7r0o22jmum9isawv` (`hearing_stage_sno`),
  CONSTRAINT `FKewtg0n3ys7r0o22jmum9isawv` FOREIGN KEY (`hearing_stage_sno`) REFERENCES `master_hearing_stages` (`sno`),
  CONSTRAINT `master_FKedkiusnqr1rie6qfdxes53ooa` FOREIGN KEY (`process_sno`) REFERENCES `master_process_template_details` (`sno`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `master_process_sms_config_details` (
  `sno` bigint NOT NULL AUTO_INCREMENT,
  `process_sno` bigint DEFAULT NULL,
  `hearing_stage_sno` bigint DEFAULT NULL,
  `sent_level` int DEFAULT '0',
  `peid` varchar(255) DEFAULT NULL,
  `sender_id` varchar(255) DEFAULT NULL,
  `route_id` varchar(255) DEFAULT NULL,
  `template_path` varchar(255) DEFAULT NULL,
  `template_id` varchar(255) DEFAULT NULL,
  `channel` varchar(255) DEFAULT NULL,
  `dcs` int DEFAULT '0' COMMENT 'Default is 0 for normal message,\n Set 8 for unicode sms',
  `flash_sms` int DEFAULT '0' COMMENT 'Default is 0 for normal sms, \\nSet 1 for immediate display',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` int DEFAULT '1',
  PRIMARY KEY (`sno`),
  UNIQUE KEY `template_id_UNIQUE` (`template_id`,`process_sno`),
  KEY `master_FKcq6q4pt2s1liawu4bckbvp1ot` (`process_sno`),
  KEY `FK35p5n4folv81gdepft1lf9xnh` (`hearing_stage_sno`),
  CONSTRAINT `FK35p5n4folv81gdepft1lf9xnh` FOREIGN KEY (`hearing_stage_sno`) REFERENCES `master_hearing_stages` (`sno`),
  CONSTRAINT `master_FKcq6q4pt2s1liawu4bckbvp1ot` FOREIGN KEY (`process_sno`) REFERENCES `master_process_template_details` (`sno`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `master_process_template_details` (
  `sno` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `step_name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`sno`),
  UNIQUE KEY `master_UKfyv1kpwredybvg2cw0njc2ero` (`name`),
  UNIQUE KEY `UK6omofx8qheck034n78flfu0fw` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `master_process_whatsapp_config_details` (
  `sno` bigint NOT NULL AUTO_INCREMENT,
  `process_sno` bigint NOT NULL,
  `hearing_stage_sno` bigint DEFAULT NULL,
  `sent_level` int DEFAULT '0',
  `template_name` varchar(255) DEFAULT NULL,
  `template_path` varchar(255) DEFAULT NULL,
  `template_lang` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` int DEFAULT '1',
  PRIMARY KEY (`sno`),
  KEY `master_FKhtprtbjtj1f4117qto2f8m9v6` (`process_sno`),
  KEY `FKad1kjekj2b4xkw1iyqf7802cf` (`hearing_stage_sno`),
  CONSTRAINT `FKad1kjekj2b4xkw1iyqf7802cf` FOREIGN KEY (`hearing_stage_sno`) REFERENCES `master_hearing_stages` (`sno`),
  CONSTRAINT `master_FKhtprtbjtj1f4117qto2f8m9v6` FOREIGN KEY (`process_sno`) REFERENCES `master_process_template_details` (`sno`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `process_excel_mapping` (
  `sno` bigint NOT NULL AUTO_INCREMENT,
  `process_id` bigint DEFAULT NULL,
  `excel_field_name` varchar(255) NOT NULL,
  `db_field_name` varchar(255) DEFAULT NULL,
  `is_key` int DEFAULT '0',
  `is_mobile` int DEFAULT '0',
  `is_mandatory` int DEFAULT '0',
  `is_attachment` int DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`sno`),
  KEY `FK6jn747mw2cdewfhe8aeob1n5x` (`process_id`),
  CONSTRAINT `FK6jn747mw2cdewfhe8aeob1n5x` FOREIGN KEY (`process_id`) REFERENCES `master_process_template_details` (`sno`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `scheduled_notice_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `scheduled_notice_id` bigint NOT NULL,
  `agreement_number` varchar(255) NOT NULL,
  `processed_at` datetime(6) DEFAULT NULL,
  `excel_data` text,
  `identifier` bigint DEFAULT NULL,
  `status` enum('EXCELUPLOADED','EXCELPROCESSING','EXCELCOMPLETED','EXCELFAILED','PENDING','PROCESSING','COMPLETED','FAILED') NOT NULL,
  `failure_reason` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `attachements` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKfa9p95naj6ocinrmic9sc8g8t` (`scheduled_notice_id`),
  CONSTRAINT `FKfa9p95naj6ocinrmic9sc8g8t` FOREIGN KEY (`scheduled_notice_id`) REFERENCES `scheduled_notices` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `scheduled_notices` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `process_sno` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint NOT NULL,
  `original_file_name` varchar(255) NOT NULL,
  `zip_file_path` varchar(255) NOT NULL,
  `extracted_folder_path` varchar(255) NOT NULL,
  `processed_at` datetime(6) DEFAULT NULL,
  `send_sms` bit(1) NOT NULL,
  `send_whatsapp` bit(1) NOT NULL,
  `identifier` bigint DEFAULT NULL,
  `status` enum('EXCELUPLOADED','EXCELPROCESSING','EXCELCOMPLETED','EXCELFAILED','PENDING','PROCESSING','COMPLETED','FAILED') NOT NULL,
  `failure_reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `send_error_mail_details` (
  `sno` bigint NOT NULL AUTO_INCREMENT,
  `type` varchar(45) DEFAULT NULL,
  `schedule_sno` bigint DEFAULT NULL,
  `mail_template_sno` bigint DEFAULT NULL,
  `loan_no` bigint DEFAULT NULL,
  `process_sno` bigint DEFAULT NULL,
  `send_at` datetime(6) DEFAULT NULL,
  `send_cc` longtext,
  `send_to` longtext,
  `subject` varchar(300) DEFAULT NULL,
  `message` longtext,
  `error_message` longtext,
  `created_by` bigint DEFAULT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`sno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `send_error_sms_details` (
  `sno` bigint NOT NULL AUTO_INCREMENT,
  `type` varchar(45) DEFAULT NULL,
  `schedule_sno` bigint DEFAULT NULL,
  `process_sno` bigint DEFAULT NULL,
  `sms_template_sno` bigint DEFAULT NULL,
  `loan_no` bigint DEFAULT NULL,
  `send_to` varchar(15) DEFAULT NULL,
  `message` longtext,
  `send_at` datetime(6) DEFAULT NULL,
  `error_message` longtext,
  `created_by` bigint DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`sno`)
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `send_error_whatsapp_details` (
  `sno` bigint NOT NULL AUTO_INCREMENT,
  `type` varchar(45) DEFAULT NULL,
  `schedule_sno` bigint DEFAULT NULL,
  `process_sno` bigint DEFAULT NULL,
  `whatsapp_template_sno` bigint DEFAULT NULL,
  `loan_no` bigint DEFAULT NULL,
  `send_to` varchar(15) DEFAULT NULL,
  `message` longtext,
  `send_at` datetime(6) DEFAULT NULL,
  `error_message` longtext,
  `created_by` bigint DEFAULT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`sno`)
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `send_loan_mail_details` (
  `sno` bigint NOT NULL AUTO_INCREMENT,
  `type` varchar(45) DEFAULT NULL,
  `process_sno` bigint DEFAULT NULL,
  `mail_template_sno` bigint DEFAULT NULL,
  `loan_no` bigint DEFAULT NULL,
  `send_to` longtext,
  `send_cc` longtext,
  `subject` varchar(300) DEFAULT NULL,
  `message` longtext,
  `send_at` timestamp NULL DEFAULT NULL,
  `send_status` int DEFAULT NULL,
  `send_response` tinytext,
  `created_by` bigint DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `schedule_sno` bigint DEFAULT NULL,
  PRIMARY KEY (`sno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `send_loan_sms_details` (
  `sno` bigint NOT NULL AUTO_INCREMENT,
  `type` varchar(45) DEFAULT NULL,
  `process_sno` bigint DEFAULT NULL,
  `sms_template_sno` bigint DEFAULT NULL,
  `loan_no` bigint DEFAULT NULL,
  `send_to` varchar(15) DEFAULT NULL,
  `message` longtext,
  `send_at` timestamp NULL DEFAULT NULL,
  `send_status` int DEFAULT NULL,
  `send_response` longtext,
  `created_by` bigint DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `error_message` longtext,
  `sms_ack_id` varchar(50) DEFAULT NULL,
  `schedule_sno` bigint DEFAULT NULL,
  `ack_id` varchar(255) DEFAULT NULL,
  `received_status` varchar(50) DEFAULT NULL,
  `received_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`sno`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `send_loan_whatsapp_details` (
  `sno` bigint NOT NULL AUTO_INCREMENT,
  `type` varchar(45) DEFAULT NULL,
  `process_sno` bigint DEFAULT NULL,
  `whatsapp_template_sno` bigint DEFAULT NULL,
  `loan_no` bigint DEFAULT NULL,
  `send_to` varchar(15) DEFAULT NULL,
  `message` longtext,
  `send_at` timestamp NULL DEFAULT NULL,
  `send_status` int DEFAULT NULL,
  `send_response` longtext,
  `sms_ack_id` varchar(50) DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `schedule_sno` bigint DEFAULT NULL,
  `ack_id` varchar(255) DEFAULT NULL,
  `received_status` varchar(50) DEFAULT NULL,
  `received_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`sno`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `send_non_loan_mail_details` (
  `sno` bigint NOT NULL AUTO_INCREMENT,
  `type` varchar(45) DEFAULT NULL,
  `process_sno` bigint DEFAULT NULL,
  `mail_template_sno` bigint DEFAULT NULL,
  `send_to` longtext,
  `send_cc` longtext,
  `subject` varchar(300) DEFAULT NULL,
  `message` longtext,
  `send_at` timestamp NULL DEFAULT NULL,
  `send_status` int DEFAULT NULL,
  `send_response` tinytext,
  `created_by` bigint DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `schedule_sno` bigint DEFAULT NULL,
  PRIMARY KEY (`sno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `send_non_loan_sms_details` (
  `sno` bigint NOT NULL AUTO_INCREMENT,
  `type` varchar(45) DEFAULT NULL,
  `process_sno` bigint DEFAULT NULL,
  `sms_template_sno` bigint DEFAULT NULL,
  `send_to` varchar(15) DEFAULT NULL,
  `message` longtext,
  `send_at` timestamp NULL DEFAULT NULL,
  `send_status` int DEFAULT NULL,
  `send_response` longtext,
  `sms_ack_id` varchar(50) DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `error_message` longtext,
  `schedule_sno` bigint DEFAULT NULL,
  `ack_id` varchar(255) DEFAULT NULL,
  `received_status` varchar(50) DEFAULT NULL,
  `received_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`sno`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `send_non_loan_whatsapp_details` (
  `sno` bigint NOT NULL AUTO_INCREMENT,
  `type` varchar(45) DEFAULT NULL,
  `process_sno` bigint DEFAULT NULL,
  `whatsapp_template_sno` bigint DEFAULT NULL,
  `send_to` varchar(15) DEFAULT NULL,
  `message` longtext,
  `send_at` timestamp NULL DEFAULT NULL,
  `send_status` int DEFAULT NULL,
  `send_response` longtext,
  `ack_id` varchar(255) DEFAULT NULL,
  `received_status` varchar(50) DEFAULT NULL,
  `received_at` timestamp NULL DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `schedule_sno` bigint DEFAULT NULL,
  PRIMARY KEY (`sno`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `status_report_sms` (
  `sno` bigint NOT NULL AUTO_INCREMENT,
  `request_params` tinytext,
  `request_body` tinytext,
  `status` varchar(45) DEFAULT '0',
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `process_date` timestamp NULL DEFAULT NULL,
  `complete_date` timestamp NULL DEFAULT NULL,
  `description` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`sno`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `status_report_whatsapp` (
  `sno` bigint NOT NULL AUTO_INCREMENT,
  `request_params` tinytext,
  `request_body` tinytext,
  `status` varchar(45) DEFAULT '0',
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `process_date` timestamp NULL DEFAULT NULL,
  `complete_date` timestamp NULL DEFAULT NULL,
  `description` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`sno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
