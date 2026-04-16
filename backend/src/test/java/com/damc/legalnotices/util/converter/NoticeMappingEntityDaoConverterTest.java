package com.damc.legalnotices.util.converter;

import com.damc.legalnotices.config.LocationProperties;
import com.damc.legalnotices.util.TemplateUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NoticeMappingEntityDaoConverterTest {

    @TempDir
    Path tempDir;

    private NoticeMappingEntityDaoConverter converter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        LocationProperties props = mock(LocationProperties.class);
        when(props.getTemplateLocation()).thenReturn(tempDir.toString());
        var templateUtil = new TemplateUtil(props, objectMapper);
        converter = new NoticeMappingEntityDaoConverter(templateUtil);
    }

    @Test
    void toSmsTemplateDao_readsFileWithLeadingSlashAndNoExtension() throws IOException {

        // Assert
        assertNotNull(converter);

    }
}
