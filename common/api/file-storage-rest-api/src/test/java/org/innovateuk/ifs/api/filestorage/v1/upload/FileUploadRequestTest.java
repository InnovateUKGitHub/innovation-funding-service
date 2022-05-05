package org.innovateuk.ifs.api.filestorage.v1.upload;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.innovateuk.ifs.api.filestorage.util.FileUploadRequestBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class FileUploadRequestTest {

    @Test
    void testEquals() {
        EqualsVerifier.forClass(FileUploadRequest.class).verify();
    }

    @Test
    void json() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValueAsString(FileUploadRequestBuilder.fromResource("test".getBytes(), "testUser").build());
    }
}