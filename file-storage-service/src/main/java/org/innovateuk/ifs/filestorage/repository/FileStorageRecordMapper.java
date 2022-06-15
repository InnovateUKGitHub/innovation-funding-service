package org.innovateuk.ifs.filestorage.repository;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.innovateuk.ifs.api.filestorage.v1.download.FileDownloadResponse;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.springframework.web.server.ResponseStatusException;

/**
 * Map entity to its respective request and response to and from the DB.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileStorageRecordMapper {

    public static FileStorageRecord to(FileUploadRequest fileUploadRequest, String providerStorageLocation) {
        FileStorageRecord fileStorageRecord = internal(fileUploadRequest);
        fileStorageRecord.storageLocation(providerStorageLocation);
        return fileStorageRecord;
    }

    public static FileStorageRecord fromError(FileUploadRequest fileUploadRequest, ResponseStatusException exception) {
        FileStorageRecord fileStorageRecord = internal(fileUploadRequest);
        String message = exception.getReason();
        if (message == null) {
            message = "unknown";
        }
        fileStorageRecord.error(message.substring(0, Math.min(message.length(), 250)));
        return fileStorageRecord;
    }

    public static FileDownloadResponse from(FileStorageRecord fileStorageRecord, byte[] payload) {
        return FileDownloadResponse.builder()
                .fileId(fileStorageRecord.fileUuid())
                .mimeType(fileStorageRecord.mimeType())
                .fileSizeBytes(fileStorageRecord.fileSizeBytes())
                .fileName(fileStorageRecord.fileName())
                .md5Checksum(fileStorageRecord.md5Checksum())
                .payload(payload)
                .build();
    }

    private static FileStorageRecord internal(FileUploadRequest fileUploadRequest) {
        FileStorageRecord fileStorageRecord = new FileStorageRecord();
        fileStorageRecord.fileUuid(fileUploadRequest.getFileId());
        fileStorageRecord.systemId(fileUploadRequest.getSystemId());
        fileStorageRecord.userId(fileUploadRequest.getUserId());
        fileStorageRecord.mimeType(fileUploadRequest.getMimeType());
        fileStorageRecord.fileSizeBytes(fileUploadRequest.getFileSizeBytes());
        fileStorageRecord.fileName(fileUploadRequest.getFileName());
        fileStorageRecord.md5Checksum(fileUploadRequest.getMd5Checksum());
        return fileStorageRecord;
    }

}
