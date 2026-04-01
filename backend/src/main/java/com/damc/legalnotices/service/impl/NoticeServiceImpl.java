package com.damc.legalnotices.service.impl;

import com.damc.legalnotices.dao.ProcessTemplateDao;
import com.damc.legalnotices.dao.ScheduledNoticeDao;
import com.damc.legalnotices.dto.NoticeValidationResponseDto;
import com.damc.legalnotices.dto.NoticeValidationRowDto;
import com.damc.legalnotices.entity.LoginDetail;
import com.damc.legalnotices.entity.ScheduledNotice;
import com.damc.legalnotices.entity.ScheduledNoticeItem;
import com.damc.legalnotices.enums.ProcessingStatus;
import com.damc.legalnotices.repository.LoginDetailRepository;
import com.damc.legalnotices.repository.MasterProcessTemplateDetailRepository;
import com.damc.legalnotices.repository.ScheduledNoticeItemRepository;
import com.damc.legalnotices.repository.ScheduledNoticeRepository;
import com.damc.legalnotices.service.NoticeService;
import com.damc.legalnotices.util.EntityDaoConverter;
import com.damc.legalnotices.util.ExcelAgreementRow;
import com.damc.legalnotices.util.ExcelParserUtil;
import com.damc.legalnotices.util.ZipExtractorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    @Value("${app.storage.zip-upload-dir}")
    private String zipUploadDir;

    @Value("${app.storage.extracted-dir}")
    private String extractedDir;

    private final MasterProcessTemplateDetailRepository processTemplateRepository;
    private final ScheduledNoticeRepository scheduledNoticeRepository;
    private final ScheduledNoticeItemRepository scheduledNoticeItemRepository;
    private final LoginDetailRepository loginDetailRepository;
    private final ZipExtractorUtil zipExtractorUtil;
    private final ExcelParserUtil excelParserUtil;
    private final EntityDaoConverter entityDaoConverter;

    @Override
    @Transactional
    public NoticeValidationResponseDto scheduleNotice(Long processSno,
                                                      Boolean sendSms,
                                                      Boolean sendWhatsapp,
                                                      MultipartFile zipFile,
                                                      String loginName) {
        if (zipFile == null || zipFile.isEmpty()) {
            throw new IllegalArgumentException("ZIP file is required");
        }
        if (!Boolean.TRUE.equals(sendSms) && !Boolean.TRUE.equals(sendWhatsapp)) {
            throw new IllegalArgumentException("Please select at least one channel (SMS or WhatsApp)");
        }

        processTemplateRepository.findById(processSno)
                .orElseThrow(() -> new IllegalArgumentException("Invalid notice type"));

        LoginDetail user = loginDetailRepository.findByLoginNameAndEnabledTrue(loginName)
                .orElseThrow(() -> new IllegalArgumentException("Logged in user not found"));

        Path savedZipPath = saveZipFile(zipFile);
        Path extractedPath = extractZip(savedZipPath);

        Path excelPath = findExcel(extractedPath);
        List<ExcelAgreementRow> excelRows;
        try {
            excelRows = excelParserUtil.parse(excelPath);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to parse excel from ZIP: " + ex.getMessage());
        }
        if (excelRows.isEmpty()) {
            throw new IllegalArgumentException("Excel has no valid agreement rows");
        }

        ScheduledNotice scheduledNotice = new ScheduledNotice();
        scheduledNotice.setProcessSno(processSno);
        scheduledNotice.setOriginalFileName(zipFile.getOriginalFilename());
        scheduledNotice.setZipFilePath(savedZipPath.toAbsolutePath().toString());
        scheduledNotice.setExtractedFolderPath(extractedPath.toAbsolutePath().toString());
        scheduledNotice.setSendSms(Boolean.TRUE.equals(sendSms));
        scheduledNotice.setSendWhatsapp(Boolean.TRUE.equals(sendWhatsapp));
        scheduledNotice.setCreatedBy(user.getId());
        scheduledNotice.setCreatedAt(LocalDateTime.now());
        scheduledNotice.setStatus(ProcessingStatus.PENDING);
        scheduledNotice = scheduledNoticeRepository.save(scheduledNotice);

        List<NoticeValidationRowDto> validationRows = new ArrayList<>();
        for (ExcelAgreementRow row : excelRows) {
            boolean exists = row.getPdfFileName() != null && !row.getPdfFileName().isBlank()
                    && Files.exists(extractedPath.resolve(row.getPdfFileName()).normalize());

            ScheduledNoticeItem item = new ScheduledNoticeItem();
            item.setScheduledNotice(scheduledNotice);
            item.setAgreementNumber(row.getAgreementNumber());
            item.setCustomerName(row.getCustomerName());
            item.setMobileSms(row.getMobileSms());
            item.setMobileWhatsapp(row.getMobileWhatsapp());
            item.setPdfFileName(row.getPdfFileName());
            item.setPdfFilePath(exists ? extractedPath.resolve(row.getPdfFileName()).normalize().toString() : null);
            item.setDocumentPresent(exists);
            item.setStatus(exists ? ProcessingStatus.PENDING : ProcessingStatus.FAILED);
            item.setFailureReason(exists ? null : "PDF file not found in extracted zip");
            if (!exists) {
                item.setProcessedAt(LocalDateTime.now());
            }
            scheduledNoticeItemRepository.save(item);

            validationRows.add(NoticeValidationRowDto.builder()
                    .agreementNumber(row.getAgreementNumber())
                    .customerName(row.getCustomerName())
                    .mobileSms(row.getMobileSms())
                    .mobileWhatsapp(row.getMobileWhatsapp())
                    .expectedPdfFile(row.getPdfFileName())
                    .documentPresent(exists)
                    .build());
        }

        validationRows.sort(Comparator.comparing(NoticeValidationRowDto::getAgreementNumber));

        return NoticeValidationResponseDto.builder()
                .scheduleId(scheduledNotice.getId())
                .originalZipFile(scheduledNotice.getOriginalFileName())
                .extractedFolder(scheduledNotice.getExtractedFolderPath())
                .status(scheduledNotice.getStatus().name())
                .rows(validationRows)
                .build();
    }

    @Override
    public List<ScheduledNoticeDao> getScheduledNotices() {
        return scheduledNoticeRepository.findAll().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(entityDaoConverter::toScheduledNoticeDao)
                .toList();
    }

    @Override
    public List<ProcessTemplateDao> getNoticeTypes() {
        return processTemplateRepository.findAll().stream()
                .map(process -> ProcessTemplateDao.builder()
                        .id(process.getId())
                        .name(process.getName())
                        .build())
                .toList();
    }

    private Path saveZipFile(MultipartFile zipFile) {
        try {
            String original = zipFile.getOriginalFilename();
            if (original == null || !original.toLowerCase().endsWith(".zip")) {
                throw new IllegalArgumentException("Only ZIP file upload is allowed");
            }

            Path uploadDir = Paths.get(zipUploadDir);
            Files.createDirectories(uploadDir);
            String fileName = UUID.randomUUID() + "_" + original;
            Path targetPath = uploadDir.resolve(fileName);
            Files.copy(zipFile.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return targetPath;
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to save ZIP file: " + ex.getMessage());
        }
    }

    private Path extractZip(Path zipPath) {
        try {
            Path targetDir = Paths.get(extractedDir).resolve(UUID.randomUUID().toString());
            zipExtractorUtil.extract(zipPath, targetDir);
            return targetDir;
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to extract ZIP file: " + ex.getMessage());
        }
    }

    private Path findExcel(Path extractedPath) {
        try (var stream = Files.walk(extractedPath)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(path -> {
                        String name = path.getFileName().toString().toLowerCase();
                        return name.endsWith(".xlsx") || name.endsWith(".xls");
                    })
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Excel file not found in ZIP"));
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to inspect extracted ZIP: " + ex.getMessage());
        }
    }
}
