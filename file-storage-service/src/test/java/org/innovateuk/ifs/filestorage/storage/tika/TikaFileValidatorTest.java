package org.innovateuk.ifs.filestorage.storage.tika;

import org.innovateuk.ifs.filestorage.cfg.StorageServiceConfiguration;
import org.innovateuk.ifs.filestorage.exception.MimeMismatchException;
import org.innovateuk.ifs.filestorage.storage.StorageService;
import org.innovateuk.ifs.filestorage.storage.StorageServiceHelper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.util.MimeType;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Import(StorageServiceConfiguration.class)
class TikaFileValidatorTest {

    private byte[] JSON = "{some: json}".getBytes(StandardCharsets.UTF_8);

    @MockBean
    private StorageService storageService;

    @MockBean
    private StorageServiceHelper storageServiceHelper;

    @Autowired
    private TikaFileValidator tikaFileValidator;

    @Test
    void validatePayload() {
        tikaFileValidator.validatePayload(MediaType.APPLICATION_JSON_VALUE, JSON, "foo.json");
    }

    @Test
    void validatePayloadFail() {
        assertThrows(
                MimeMismatchException.class,
                () -> tikaFileValidator.validatePayload(MediaType.IMAGE_GIF_VALUE, JSON, "foo.json")
        );
    }
}