package org.innovateuk.ifs.filestorage.repository;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.innovateuk.ifs.api.filestorage.v1.download.FileDownloadResponse;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.filestorage.storage.WritableStorageProvider;

/**
 * Map entity to its respective request and response to and from the DB.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileStorageRecordMapper {

    public static FileStorageRecord to(FileUploadRequest fileUploadRequest, WritableStorageProvider writableStorageProvider) {
        FileStorageRecord fileStorageRecord = new FileStorageRecord();
        fileStorageRecord.fileUuid(fileUploadRequest.fileId().toString());
        fileStorageRecord.systemId(fileUploadRequest.systemId());
        fileStorageRecord.userId(fileUploadRequest.userId());
        fileStorageRecord.mimeType(fileUploadRequest.mimeType().toString());
        fileStorageRecord.fileSizeBytes(fileUploadRequest.fileSizeBytes());
        fileStorageRecord.fileName(fileUploadRequest.fileName());
        fileStorageRecord.md5Checksum(fileUploadRequest.md5Checksum());
        fileStorageRecord.storageProvider(writableStorageProvider.getClass().getSimpleName());
        return fileStorageRecord;
    }

    public static FileDownloadResponse from(FileStorageRecord fileStorageRecord, byte[] payload) {
        return FileDownloadResponse.builder()
                .fileId(fileStorageRecord.fileUuid())
                .virusScanStatus(fileStorageRecord.virusScanStatus().toString())
                .virusScanResultMessage(fileStorageRecord.virusScanMessage())
                .mimeType(fileStorageRecord.mimeType())
                .fileSizeBytes(fileStorageRecord.fileSizeBytes())
                .fileName(fileStorageRecord.fileName())
                .md5Checksum(fileStorageRecord.md5Checksum())
                .payload(payload)
                .build();
    }

}
