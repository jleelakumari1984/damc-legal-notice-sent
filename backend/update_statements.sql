    alter table master_process_mail_config_details
       add column message_length integer
 
    alter table master_process_mail_config_details
       add column number_of_message integer
 
    alter table master_process_sms_config_details
       add column message_length integer
 
    alter table master_process_sms_config_details
       add column number_of_message integer
 
    alter table master_process_whatsapp_config_details
       add column message_length integer
 
    alter table master_process_whatsapp_config_details
       add column number_of_message integer

     alter table send_loan_mail_details
       add column number_of_message integer


    alter table send_loan_sms_details
       add column number_of_message integer

alter table send_loan_whatsapp_details
       add column number_of_message integer




    alter table send_non_loan_mail_details
       add column number_of_message integer
  
 
    alter table send_non_loan_sms_details
       add column message_count integer
 
 alter table send_non_loan_whatsapp_details
       add column message_count integer
 
 ALTER TABLE `master_process_excel_mapping` 
ADD COLUMN `is_customer_name` INT NULL AFTER `is_attachment`,
CHANGE COLUMN `is_key` `is_agreement` INT NULL DEFAULT '0' ;

ALTER TABLE `scheduled_notice_items` 
ADD COLUMN `customer_name` VARCHAR(45) NULL AFTER `agreement_number`,
CHANGE COLUMN `attachements` `attachments` VARCHAR(255) NULL DEFAULT NULL ;


 ALTER TABLE `master_process_mail_config_details` 
CHANGE COLUMN `number_of_message` `number_of_message` INT NOT NULL DEFAULT 1 ;
 ALTER TABLE `master_process_sms_config_details` 
CHANGE COLUMN `number_of_message` `number_of_message` INT NOT NULL DEFAULT 1 ;

 ALTER TABLE `master_process_whatsapp_config_details` 
CHANGE COLUMN `number_of_message` `number_of_message` INT NOT NULL DEFAULT 1 ;
