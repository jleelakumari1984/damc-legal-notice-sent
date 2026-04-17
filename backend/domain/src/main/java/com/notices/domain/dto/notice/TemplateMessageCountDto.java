package com.notices.domain.dto.notice;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TemplateMessageCountDto {
    List<String> fields;
    Integer noOfMessages;
    Integer messageLengths;

}
