package org.innovateuk.ifs.api.filestorage.v1.upload;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class FileUploadRequestTest {

    @Test
    void testEquals() {
        EqualsVerifier.forClass(FileUploadRequest.class).verify();
    }

    @Test
    void json() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        FileUploadRequest fileUploadRequest = FileUploadRequest.builder()
                .fileName("foo.pdf")
                .md5Checksum("ddffddf")
                .payload("dfdfdfdf".getBytes(StandardCharsets.UTF_8))
                .mimeType(MediaType.APPLICATION_JSON_VALUE)
                .userId("foo")
                .systemId("bar")
                .build();
        String json = objectMapper.writeValueAsString(fileUploadRequest);
        assertThat(json.contains("foo"), equalTo(true));
    }
}