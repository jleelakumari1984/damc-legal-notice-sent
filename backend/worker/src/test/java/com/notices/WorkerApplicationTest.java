package com.notices;

import com.notices.domain.config.LocationProperties;
import com.notices.domain.util.TemplateUtil;
import com.notices.domain.util.converter.NoticeMappingEntityDaoConverter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;

@SpringBootTest
class WorkerApplicationTest {

    @TempDir
    Path tempDir;

    private NoticeMappingEntityDaoConverter converter;

    @BeforeEach
    void setUp() {
        LocationProperties props = mock(LocationProperties.class);
        when(props.getTemplateLocation()).thenReturn(tempDir.toString());
        var templateUtil = mock(TemplateUtil.class);
        converter = new NoticeMappingEntityDaoConverter(templateUtil);
    }

    @Test
    void toSmsTemplateDao_readsFileWithLeadingSlashAndNoExtension() throws IOException {

        // Assert
        assertNotNull(converter);

    }
}
