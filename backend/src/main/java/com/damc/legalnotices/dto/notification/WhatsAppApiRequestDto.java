package com.damc.legalnotices.dto.notification;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class WhatsAppApiRequestDto {
    @Getter
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WhatsAppDocumentDto {

        private String link;

    }

    @Getter
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WhatsAppParameterDto {

        private String type;

        private String text;

        private WhatsAppDocumentDto document;

    }

    @Getter
    @Setter
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WhatsAppComponentDto {

        private String type;

        private List<WhatsAppParameterDto> parameters;

        public static WhatsAppComponentDto ofDocument(String link, String filename) {
            return WhatsAppComponentDto.builder()
                    .type("header")
                    .parameters(List.of(
                            WhatsAppParameterDto.builder()
                                    .type("document")
                                    .document(WhatsAppDocumentDto.builder().link(link).build())
                                    .build()))
                    .build();
        }
    }

    private String phone;

    private String token;

    private List<WhatsAppComponentDto> components;

    @JsonProperty("template_language")
    private String templateLanguage;

    @JsonProperty("template_name")
    private String templateName;
}
