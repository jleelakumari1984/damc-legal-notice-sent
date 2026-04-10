package com.damc.legalnotices.controller.status;

import com.damc.legalnotices.service.NotificationCallbackService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/notification-callback")
@RequiredArgsConstructor
public class NotificationCallbackController {

    private final NotificationCallbackService callbackService;
    private final ObjectMapper objectMapper;

    // ─── SMS Delivery Report ───────────────────────────────────────────────────

    /**
     * POST: SMS gateway sends DLR as JSON body.
     * {"messageid":"xxx","dlrstatus":"xxx","msisdn":"xxx","senderid":"xxx",
     *  "submittime":"xxx","delivtime":"xxx","dlrcode":"xxx"}
     */
    @PostMapping("/sms-dlr")
    public ResponseEntity<Map<String, String>> smsDlrPost(@RequestBody String body) {
        log.info("SMS DLR POST received: {}", body);
        callbackService.processSmsDeliveryReport(body);
        return ResponseEntity.ok(Map.of("status", "success"));
    }

    /**
     * GET: SMS gateway sends DLR as query params (some providers use HTTP GET callbacks).
     */
    @GetMapping("/sms-dlr")
    public ResponseEntity<Map<String, String>> smsDlrGet(
            @RequestParam(value = "messageid", required = false) String messageid,
            @RequestParam(value = "dlrstatus", required = false) String dlrstatus,
            @RequestParam(value = "msisdn", required = false) String msisdn,
            @RequestParam(value = "senderid", required = false) String senderid,
            @RequestParam(value = "submittime", required = false) String submittime,
            @RequestParam(value = "delivtime", required = false) String delivtime,
            @RequestParam(value = "dlrcode", required = false) String dlrcode) {
        try {
            Map<String, String> params = Map.of(
                    "messageid", messageid != null ? messageid : "",
                    "dlrstatus", dlrstatus != null ? dlrstatus : "",
                    "msisdn", msisdn != null ? msisdn : "",
                    "senderid", senderid != null ? senderid : "",
                    "submittime", submittime != null ? submittime : "",
                    "delivtime", delivtime != null ? delivtime : "",
                    "dlrcode", dlrcode != null ? dlrcode : "");
            String body = objectMapper.writeValueAsString(params);
            log.info("SMS DLR GET received, mapped to: {}", body);
            callbackService.processSmsDeliveryReport(body);
        } catch (JsonProcessingException ex) {
            log.error("SMS DLR GET: failed to map params to JSON", ex);
        }
        return ResponseEntity.ok(Map.of("status", "success"));
    }

    // ─── WhatsApp Status Webhook ───────────────────────────────────────────────

    /**
     * GET: Meta webhook verification challenge.
     */
    @GetMapping("/whatsapp")
    public ResponseEntity<String> whatsAppVerify(
            @RequestParam(value = "hub.mode", required = false) String mode,
            @RequestParam(value = "hub.verify_token", required = false) String verifyToken,
            @RequestParam(value = "hub.challenge", required = false) String challenge) {
        log.info("WhatsApp webhook verify: mode={} challenge={}", mode, challenge);
        if ("subscribe".equals(mode) && challenge != null) {
            return ResponseEntity.ok(challenge);
        }
        return ResponseEntity.ok("OK");
    }

    /**
     * POST: Meta sends WhatsApp status updates.
     * {"object":"whatsapp_business_account","entry":[...statuses with id/status/timestamp...]}
     */
    @PostMapping("/whatsapp")
    public ResponseEntity<Map<String, String>> whatsAppCallback(@RequestBody String body) {
        log.info("WhatsApp status callback POST received: {}", body);
        callbackService.processWhatsAppStatusCallback(body);
        return ResponseEntity.ok(Map.of("status", "success"));
    }

    // ─── Re-parse missed ack_ids ───────────────────────────────────────────────

    /**
     * POST: Re-parse send_response for SMS records where ack_id was not captured at send time.
     * Returns count of records updated.
     */
    @PostMapping("/reparse-sms-ackids")
    public ResponseEntity<Map<String, Object>> reparseSmsAckIds() {
        int updated = callbackService.reParseSmsMissingAckIds();
        return ResponseEntity.ok(Map.of("status", "success", "updated", updated));
    }

    /**
     * POST: Re-parse send_response for WhatsApp records where ack_id was not captured at send time.
     * Returns count of records updated.
     */
    @PostMapping("/reparse-whatsapp-ackids")
    public ResponseEntity<Map<String, Object>> reparseWhatsAppAckIds() {
        int updated = callbackService.reParseWhatsAppMissingAckIds();
        return ResponseEntity.ok(Map.of("status", "success", "updated", updated));
    }
}
