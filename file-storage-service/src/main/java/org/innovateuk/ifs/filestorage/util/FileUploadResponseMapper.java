package org.innovateuk.ifs.filestorage.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadResponse;
import org.innovateuk.ifs.filestorage.virusscan.VirusScanResult;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUploadResponseMapper {

    public static final FileUploadResponse build(FileUploadRequest fileUploadRequest, VirusScanResult virusScanResult) {
        return FileUploadResponse.builder()
                .fileId(fileUploadRequest.fileId())
                .virusScanStatus(virusScanResult.virusScanStatus())
                .virusScanResult(virusScanResult.virusScanResultMessage())
                .mimeType(fileUploadRequest.mimeType())
                .fileSizeBytes(fileUploadRequest.fileSizeBytes())
                .fileName(fileUploadRequest.fileName())
                .checksum(fileUploadRequest.md5Checksum()).build();
    }

}
