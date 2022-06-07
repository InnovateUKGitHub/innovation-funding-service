package org.innovateuk.ifs.filestorage.util;

import com.google.common.io.ByteStreams;
import org.innovateuk.ifs.api.filestorage.util.FileHashing;
import org.innovateuk.ifs.api.filestorage.util.FileUploadRequestBuilder;
import org.innovateuk.ifs.api.filestorage.v1.download.FileDownloadResponse;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.MediaType.IMAGE_JPEG;

public class TestHelper {

    private static final String TEST_IMAGE = "test.jpg";

    public static final FileUploadRequest.FileUploadRequestBuilder builder(UUID uuid) throws IOException {
        FileUploadRequest.FileUploadRequestBuilder builder
                = FileUploadRequestBuilder.fromResource(uuid, new ClassPathResource(TEST_IMAGE), IMAGE_JPEG, "testUser");
        return builder.userId(TestHelper.class.getSimpleName());
    }

    public static final FileUploadRequest build(UUID uuid) throws IOException {
        FileUploadRequest.FileUploadRequestBuilder builder
                = FileUploadRequestBuilder.fromResource(uuid, new ClassPathResource(TEST_IMAGE), IMAGE_JPEG, "testUser");
        return builder(uuid).build();
    }

    public static FileUploadRequest build() throws IOException {
        return build(UUID.randomUUID());
    }

    public static FileDownloadResponse buildDownLoadResponse(UUID uuid) throws IOException {
        Resource resource = new ClassPathResource(TEST_IMAGE);
        byte[] payload = ByteStreams.toByteArray(resource.getInputStream());
        return FileDownloadResponse.builder()
                .payload(payload)
                .md5Checksum(FileHashing.fileHash64(payload))
                .fileName(resource.getFilename())
                .fileId(uuid.toString())
                .mimeType(IMAGE_JPEG.toString())
                .fileSizeBytes(payload.length)
                .build();
    }

    public static void headerAssert(ResponseEntity<?> responseEntity, String header, Object expected) {
        assertThat(responseEntity.getHeaders().get(header).size(), equalTo(1));
        assertThat(responseEntity.getHeaders().get(header).get(0), equalTo(expected));
    }

    public static String activeProfilesString(List<String> profiles) {
        return AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME
                + "=" +
                profiles.stream().collect(Collectors.joining(","));
    }
}
