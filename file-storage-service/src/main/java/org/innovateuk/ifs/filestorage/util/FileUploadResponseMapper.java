package org.innovateuk.ifs.filestorage.util;

import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadResponse;
import org.innovateuk.ifs.filestorage.virusscan.VirusScanResult;

public class FileUploadResponseMapper {

    public static final FileUploadResponse build(FileUploadRequest fileUploadRequest, VirusScanResult virusScanResult) {
        return FileUploadResponse.builder()
                .fileId(fileUploadRequest.fileId())
                .virusScanStatus(virusScanResult.getVirusScanStatus())
                .virusScanResult(virusScanResult.getVirusScanResultMessage())
                .mimeType(fileUploadRequest.mimeType())
                .fileSizeBytes(fileUploadRequest.fileSizeBytes())
                .fileName(fileUploadRequest.fileName())
                .checksum(fileUploadRequest.checksum()).build();
    }

}
