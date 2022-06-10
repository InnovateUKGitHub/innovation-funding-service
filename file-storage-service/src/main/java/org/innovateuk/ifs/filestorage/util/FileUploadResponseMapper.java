package org.innovateuk.ifs.filestorage.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUploadResponseMapper {

    public static final FileUploadResponse build(FileUploadRequest fileUploadRequest) {
        return FileUploadResponse.builder()
                .fileId(fileUploadRequest.getFileId())
                .mimeType(fileUploadRequest.getMimeType())
                .fileSizeBytes(fileUploadRequest.getFileSizeBytes())
                .fileName(fileUploadRequest.getFileName())
                .md5Checksum(fileUploadRequest.getMd5Checksum()).build();
    }

}
