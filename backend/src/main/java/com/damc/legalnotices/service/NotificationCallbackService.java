package com.damc.legalnotices.service;

public interface NotificationCallbackService {

    /**
     * Process SMS delivery report callback.
     * Expected format: {"messageid":"xxx","dlrstatus":"xxx","msisdn":"xxx",
     * "senderid":"xxx","submittime":"xxx","delivtime":"xxx","dlrcode":"xxx"}
     * Matches ack_id = messageid, sets received_status = "RECEIVED", received_at = delivtime.
     */
    void processSmsDeliveryReport(String requestBody);

    /**
     * Process WhatsApp status callback (Meta webhook).
     * Expected format: {"object":"whatsapp_business_account","entry":[...statuses with id/status/timestamp...]}
     * Matches ack_id = status.id, sets received_status = status.status, received_at = status.timestamp.
     */
    void processWhatsAppStatusCallback(String requestBody);

    /**
     * Re-parse send_response for SMS records where ack_id is NULL.
     * Returns count of records updated.
     */
    int reParseSmsMissingAckIds();

    /**
     * Re-parse send_response for WhatsApp records where ack_id is NULL.
     * Returns count of records updated.
     */
    int reParseWhatsAppMissingAckIds();
}
