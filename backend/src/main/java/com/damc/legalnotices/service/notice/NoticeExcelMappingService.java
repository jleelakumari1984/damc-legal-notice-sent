package com.damc.legalnotices.service.notice;

import com.damc.legalnotices.dao.notice.NoticeExcelMappingDao;
import com.damc.legalnotices.dto.notice.NoticeExcelMappingDto;

import java.util.List;

public interface NoticeExcelMappingService {

    List<NoticeExcelMappingDao> getByProcessId(Long processId);

    NoticeExcelMappingDao getById(Long id);

    NoticeExcelMappingDao create(NoticeExcelMappingDto request);

    NoticeExcelMappingDao update(Long id, NoticeExcelMappingDto request);

    void delete(Long id);
}
