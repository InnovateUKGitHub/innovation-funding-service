package org.innovateuk.ifs.file.transactional;

import com.google.common.io.ByteStreams;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.IfsConstants;
import org.innovateuk.ifs.api.filestorage.util.FileHashing;
import org.innovateuk.ifs.api.filestorage.v1.delete.FileDeletion;
import org.innovateuk.ifs.api.filestorage.v1.download.FileDownload;
import org.innovateuk.ifs.api.filestorage.v1.download.FileDownloadResponse;
import org.innovateuk.ifs.api.filestorage.v1.delete.FileDeletionRequest;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUpload;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadResponse;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * The class is an implementation of FileService that, based upon a given fileStorageStrategy, is able to
 * validate, store and retrieve files.
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileDownload fileDownloadFeign;

    @Autowired
    private FileUpload fileUploadFeign;

    @Autowired
    private FileDeletion fileDeletionFeign;

    @Autowired
    private FileServiceTransactionHelper fileServiceTransactionHelper;

    @Override
    public ServiceResult<FileEntry> createFile(FileEntryResource resource, Supplier<InputStream> inputStreamSupplier) {
        return createOrUpdate(fileServiceTransactionHelper.persistInitial(), resource, inputStreamSupplier);
    }

    @Override
    public ServiceResult<FileEntry> updateFile(FileEntryResource fileToUpdate, Supplier<InputStream> inputStreamSupplier) {
        try {
            return createOrUpdate(fileServiceTransactionHelper.updateExisting(fileToUpdate.getId()), fileToUpdate, inputStreamSupplier);
        } catch (NoSuchElementException e) {
            return serviceFailure(new Error(FILES_UNABLE_TO_FIND_FILE_ENTRY_ID_FROM_FILE));
        }
    }

    private ServiceResult<FileEntry> createOrUpdate(FileEntry fileEntry, FileEntryResource resource, Supplier<InputStream> inputStreamSupplier) {
        try {
            byte[] payload = ByteStreams.toByteArray(inputStreamSupplier.get());
            FileUploadRequest.FileUploadRequestBuilder fileUploadRequestBuilder = FileUploadRequest.builder()
                    .fileId(fileEntry.getFileUuid())
                    .systemId(IfsConstants.IFS_SYSTEM_USER)
                    .userId(FileServiceImpl.class.getSimpleName())
                    .payload(payload)
                    .mimeType(resource.getMediaType())
                    .fileSizeBytes(resource.getFilesizeBytes())
                    .fileName(resource.getName())
                    .md5Checksum(FileHashing.fileHash64(payload));
            ResponseEntity<FileUploadResponse> fileUploadResponse = fileUploadFeign.fileUpload(fileUploadRequestBuilder.build());
            return serviceSuccess(
                fileServiceTransactionHelper.updateResponse(fileEntry.getId(),
                        fileUploadResponse.getBody().getMd5Checksum(),
                        fileUploadResponse.getBody().getFileName(),
                        fileUploadResponse.getBody().getMimeType(),
                        fileUploadResponse.getBody().getFileSizeBytes()
                )
            );
        } catch (FeignException | IOException | NoSuchElementException e) {
            log.error("Failed to save file", e);
            return ServiceResult.serviceFailure(new Error(FILES_UNABLE_TO_CREATE_FILE));
        } catch (ResponseStatusException responseStatusException) {
            log.error("Failed to save file", responseStatusException);
            // Map these back to the existing client contract
            if (responseStatusException.getStatus().equals(HttpStatus.BAD_REQUEST)) {
                if (responseStatusException.getReason().contains("InvalidUploadException")) {
                    return ServiceResult.serviceFailure(new Error(FILES_INCORRECTLY_REPORTED_FILESIZE));
                }
                if (responseStatusException.getReason().contains("MimeMismatchException")) {
                    return ServiceResult.serviceFailure(new Error(FILES_INCORRECTLY_REPORTED_MEDIA_TYPE));
                }
                if (responseStatusException.getReason().contains("MimeMismatchException")) {
                    return ServiceResult.serviceFailure(new Error(FILES_INCORRECTLY_REPORTED_MEDIA_TYPE));
                }
                if (responseStatusException.getReason().contains("VirusDetectedException")) {
                    return ServiceResult.serviceFailure(new Error(FILES_FILE_QUARANTINED));
                }
            }
            return ServiceResult.serviceFailure(new Error(FILES_UNABLE_TO_CREATE_FILE));
        }
    }

    @Override
    public ServiceResult<Supplier<InputStream>> getFileByFileEntryId(Long fileEntryId) {
        try {
            FileEntry fileEntry = fileServiceTransactionHelper.find(fileEntryId);
            if (!isFileExists(fileEntry)) {
                return serviceFailure(new Error(GENERAL_NOT_FOUND));
            }
            ResponseEntity<FileDownloadResponse> fileDownloadResponse = fileDownloadFeign.fileDownloadResponse(fileEntry.getFileUuid());
            return serviceSuccess(() -> new ByteArrayInputStream(fileDownloadResponse.getBody().getPayload()));
        } catch (NoSuchElementException ex) {
            return serviceFailure(new Error(FILES_UNABLE_TO_FIND_FILE_ENTRY_ID_FROM_FILE));
        } catch (ResponseStatusException responseStatusException) {
            return serviceFailure(responseStatusException);
        }
    }

    private boolean isFileExists(FileEntry fileEntry) {
        return fileEntry.getFileUuid() != null && !fileEntry.getFileUuid().isEmpty();
    }

    @Override
    public ServiceResult<FileEntry> deleteFileIgnoreNotFound(long fileEntryId) {
        FileEntry fileEntry = fileServiceTransactionHelper.find(fileEntryId);
        String fileUuid = fileEntry.getFileUuid();
        fileServiceTransactionHelper.delete(fileEntryId);
        if (isFileExists(fileEntry)) {
            fileDeletionFeign.deleteFile(new FileDeletionRequest(fileUuid));
        }
        // Why would you return the entity after delete? return new to fit existing interface
        return serviceSuccess(fileEntry);
    }

}
