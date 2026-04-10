package com.damc.legalnotices.dto;

import java.util.List;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class WhatsAppAttachmentDto {

    private String type;
    private List<Parameter> parameters;

    @Getter
    @Setter
    @Builder
    public static class Parameter {
        private String type;
        private Document document;
    }

    @Getter
    @Setter
    @Builder
    public static class Document {
        private String link;
        private String filename;
    }

    public static WhatsAppAttachmentDto ofDocument(String link,String filename) {
        return WhatsAppAttachmentDto.builder()
                .type("header")
                .parameters(List.of(
                        Parameter.builder()
                                .type("document")
                                .document(Document.builder().link(link).filename(filename).build())
                                .build()))
                .build();
    }
}
