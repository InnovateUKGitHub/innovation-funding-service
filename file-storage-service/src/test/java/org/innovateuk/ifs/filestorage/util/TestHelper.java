package org.innovateuk.ifs.filestorage.util;

import com.google.common.io.ByteStreams;
import org.innovateuk.ifs.api.filestorage.util.FileHashing;
import org.innovateuk.ifs.api.filestorage.util.FileUploadRequestBuilder;
import org.innovateuk.ifs.api.filestorage.v1.download.FileDownloadResponse;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.filestorage.virusscan.VirusScanResult;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.MediaType.IMAGE_JPEG;

public class TestHelper {

    private static final String TEST_IMAGE = "test.jpg";

    public static final FileUploadRequest build(UUID uuid) throws IOException {
        FileUploadRequest.FileUploadRequestBuilder builder
                = FileUploadRequestBuilder.fromResource(uuid, new ClassPathResource(TEST_IMAGE), IMAGE_JPEG);
        return builder.userId(TestHelper.class.getSimpleName()).build();
    }

    public static FileUploadRequest build() throws IOException {
        return build(UUID.randomUUID());
    }

    public static FileDownloadResponse build(UUID uuid, VirusScanResult virusScanResult) throws IOException {
        Resource resource = new ClassPathResource(TEST_IMAGE);
        byte[] payload = ByteStreams.toByteArray(resource.getInputStream());
        return FileDownloadResponse.builder()
                .payload(payload)
                .checksum(FileHashing.fileHash(payload))
                .fileName(resource.getFilename())
                .fileId(uuid)
                .mimeType(IMAGE_JPEG)
                .fileSizeBytes(payload.length)
                .virusScanStatus(virusScanResult.virusScanStatus())
                .virusScanResult(virusScanResult.virusScanResultMessage())
                .build();
    }

    public static void headerAssert(ResponseEntity<?> responseEntity, String header, Object expected) {
        assertThat(responseEntity.getHeaders().get(header).size(), equalTo(1));
        assertThat(responseEntity.getHeaders().get(header).get(0), equalTo(expected));
    }
}
