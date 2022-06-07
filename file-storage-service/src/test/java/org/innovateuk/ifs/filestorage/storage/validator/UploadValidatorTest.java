package org.innovateuk.ifs.filestorage.storage.validator;

import org.innovateuk.ifs.api.filestorage.util.FileUploadRequestBuilder;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.filestorage.exception.InvalidUploadException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {UploadValidator.class})
class UploadValidatorTest {

    @Autowired
    private UploadValidator uploadValidator;

    @Test
    void validateFile() throws IOException {
        FileUploadRequest fileUploadRequest =
                FileUploadRequestBuilder
                    .fromResource("123456789".getBytes(StandardCharsets.UTF_8), "test")
                        .fileSizeBytes(9L).build();
        uploadValidator.validateFile(
                fileUploadRequest
        );
    }

    @Test
    void validateFileFailure() throws IOException {
        FileUploadRequest fileUploadRequest =
                FileUploadRequestBuilder
                        .fromResource("123456789".getBytes(StandardCharsets.UTF_8), "test")
                        .fileSizeBytes(900L).build();
        assertThrows(
            InvalidUploadException.class,
                () -> uploadValidator.validateFile(
                    fileUploadRequest
            )
        );
    }
}