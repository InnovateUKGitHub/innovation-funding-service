package org.innovateuk.ifs.api.filestorage.v1.upload;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.IOException;

class FileUploadResponseTest {

    @Test
    void equalsTest() {
        EqualsVerifier.forClass(FileUploadResponse.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }

    @Test
    void json() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValueAsString(
                FileUploadResponse.builder()
                        .md5Checksum("saad")
                        .fileName("asdasd")
                        .fileSizeBytes(12L)
                        .mimeType(MediaType.APPLICATION_JSON_VALUE)
                        .fileId("adaadsdsaasd")
                        .build()
        );
    }
}