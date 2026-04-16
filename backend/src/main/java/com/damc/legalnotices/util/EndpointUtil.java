package com.damc.legalnotices.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Component;

import com.damc.legalnotices.config.LocationProperties;
import com.damc.legalnotices.dao.user.UserSmsCredentialDao;
import com.damc.legalnotices.dao.user.UserWhatsAppCredentialDao;
import com.damc.legalnotices.dto.user.UserSmsCredentialDto;
import com.damc.legalnotices.dto.user.UserWhatsAppCredentialDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@AllArgsConstructor
public class EndpointUtil {
    private final LocationProperties locationProperties;
    private final ObjectMapper objectMapper;
    private static final String SMS_CREDENTIAL_FILE = "sms_credential.json";
    private static final String WHATSAPP_CREDENTIAL_FILE = "whatsapp_credential.json";

    public Path getUserDirPath(Long userId) {
        Path userDirPath = Path.of(locationProperties.getEndPoints(), String.valueOf(userId));
        if (!userDirPath.toFile().exists()) {
            userDirPath.toFile().mkdirs();
            log.info("Created directory for user {}: {}", userId, userDirPath);
        }
        return userDirPath;
    }

    public void saveSmsCredential(Long userId, UserSmsCredentialDto dto) {
        Path filePath = getUserDirPath(userId).resolve(SMS_CREDENTIAL_FILE);
        try {
            UserSmsCredentialDao existDetails = getSmsCredential(userId);
            if (existDetails != null && (dto.getPassword() == null || dto.getPassword().isBlank())) {
                dto.setPassword(existDetails.getPassword());
            }
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), dto);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save sms credential", e);

        }
    }

    public UserSmsCredentialDao getSmsCredential(Long userId) {
        Path filePath = getUserDirPath(userId).resolve(SMS_CREDENTIAL_FILE);
        if (!Files.exists(filePath)) {
            return null;
        }
        try {
            return objectMapper.readValue(filePath.toFile(), UserSmsCredentialDao.class);
        } catch (IOException e) {
            log.info("Failed to read SMS credential for user {}: {} {}", userId, filePath, e);
            return null;
        }
    }

    public void saveWhatsAppCredential(Long userId, UserWhatsAppCredentialDto dto) {
        Path filePath = getUserDirPath(userId).resolve(WHATSAPP_CREDENTIAL_FILE);
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), dto);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save WhatsApp credential", e);
        }
    }

    public UserWhatsAppCredentialDao getWhatsAppCredential(Long userId) {
        Path filePath = getUserDirPath(userId).resolve(WHATSAPP_CREDENTIAL_FILE);
        if (!Files.exists(filePath)) {
            return null;
        }
        try {
            return objectMapper.readValue(filePath.toFile(), UserWhatsAppCredentialDao.class);
        } catch (IOException e) {
            log.info("Failed to read WhatsApp credential for user {}: {} {}", userId, filePath, e);
            return null;
        }
    }
}
