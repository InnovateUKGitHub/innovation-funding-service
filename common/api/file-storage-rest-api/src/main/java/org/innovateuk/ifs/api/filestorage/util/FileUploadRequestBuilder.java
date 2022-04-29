package org.innovateuk.ifs.api.filestorage.util;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUploadRequestBuilder {

    private static final String DEFAULT_SYSTEM_ID = "IFS";

    public static FileUploadRequest.FileUploadRequestBuilder fromResource(UUID uuid, Resource resource, MediaType mediaType) throws IOException {
        byte[] payload = ByteStreams.toByteArray(resource.getInputStream());
        return FileUploadRequest.builder()
                .fileId(uuid)
                .fileName(resource.getFilename())
                .checksum(Hashing.sha256().hashBytes(payload).toString())
                .mimeType(mediaType)
                .payload(payload)
                .fileSizeBytes(payload.length)
                .systemId(DEFAULT_SYSTEM_ID);
    }

    public static FileUploadRequest.FileUploadRequestBuilder fromResource(Resource resource, MediaType mediaType) throws IOException {
        return fromResource(UUID.randomUUID(), resource, mediaType);
    }

}
