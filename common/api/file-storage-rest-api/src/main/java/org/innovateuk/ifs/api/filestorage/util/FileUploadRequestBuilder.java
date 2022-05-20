package org.innovateuk.ifs.api.filestorage.util;

import com.google.common.io.ByteStreams;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;

import java.io.IOException;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUploadRequestBuilder {

    private static final String DEFAULT_SYSTEM_ID = "IFS";

    public static FileUploadRequest.FileUploadRequestBuilder fromResource(UUID uuid, Resource resource, MediaType mediaType, String userId) throws IOException {
        byte[] payload = ByteStreams.toByteArray(resource.getInputStream());
        //noinspection UnstableApiUsage
        return FileUploadRequest.builder()
                .fileId(uuid.toString())
                .fileName(resource.getFilename() == null ? uuid.toString() : resource.getFilename())
                .md5Checksum(FileHashing.fileHash64(payload))
                .mimeType(mediaType.toString())
                .payload(payload)
                .userId(userId)
                .fileSizeBytes(payload.length)
                .systemId(DEFAULT_SYSTEM_ID);
    }

    public static FileUploadRequest.FileUploadRequestBuilder fromResource(Resource resource, MediaType mediaType, String userId) throws IOException {
        return fromResource(UUID.randomUUID(), resource, mediaType, userId);
    }

    public static FileUploadRequest.FileUploadRequestBuilder fromResource(byte[] payload, String userId) throws IOException {
        Resource payloadResource = new ByteArrayResource(payload);
        return fromResource(UUID.randomUUID(), payloadResource,
                MediaTypeFactory.getMediaType(payloadResource).orElse(MediaType.APPLICATION_JSON), userId);
    }

    public static FileUploadRequest.FileUploadRequestBuilder fromResource(byte[] payload, MediaType mediaType, String fileName) {
        return FileUploadRequest.builder()
                .fileId(UUID.randomUUID().toString())
                .fileName(fileName)
                .md5Checksum(FileHashing.fileHash64(payload))
                .mimeType(mediaType.toString())
                .payload(payload)
                .userId("GLUSTER_MIGRATION")
                .fileSizeBytes(payload.length)
                .systemId(DEFAULT_SYSTEM_ID);
    }

}
