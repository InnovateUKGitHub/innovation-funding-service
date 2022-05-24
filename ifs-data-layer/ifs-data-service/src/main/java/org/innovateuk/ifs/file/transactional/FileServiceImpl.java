package org.innovateuk.ifs.file.transactional;

import com.google.common.io.ByteStreams;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.IfsConstants;
import org.innovateuk.ifs.api.filestorage.util.FileHashing;
import org.innovateuk.ifs.api.filestorage.v1.download.FileDownload;
import org.innovateuk.ifs.api.filestorage.v1.download.FileDownloadResponse;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUpload;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadResponse;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileService;
import org.innovateuk.ifs.file.transactional.gluster.GlusterFileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.FILES_UNABLE_TO_CREATE_FILE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.FILES_UNABLE_TO_FIND_FILE_ENTRY_ID_FROM_FILE;
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
    private GlusterFileServiceImpl glusterFileService;

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
            // TODO some sort of adaptor to map from this to CommonFailureKeys??
            log.error("Failed to save file", responseStatusException);
            return serviceFailure(responseStatusException);
        }
    }

    @Override
    public ServiceResult<Supplier<InputStream>> getFileByFileEntryId(Long fileEntryId) {
        try {
            FileEntry fileEntry = fileServiceTransactionHelper.find(fileEntryId);
            if (fileEntry.getFileUuid() == null || fileEntry.getFileUuid().isEmpty()) {
                // no uuid means it must be a gluster file
                return glusterPath(fileEntry);
            }
            ResponseEntity<FileDownloadResponse> fileDownloadResponse = fileDownloadFeign.fileDownloadResponse(fileEntry.getFileUuid());
            return serviceSuccess(() -> new ByteArrayInputStream(fileDownloadResponse.getBody().getPayload()));
        } catch (NoSuchElementException ex) {
            return serviceFailure(new Error(FILES_UNABLE_TO_FIND_FILE_ENTRY_ID_FROM_FILE));
        } catch (ResponseStatusException responseStatusException) {
            // TODO some sort of adaptor to map from this to CommonFailureKeys??
            return serviceFailure(responseStatusException);
        }
    }

    // old path via gluster - see gluster package - delete after migration
    private ServiceResult<Supplier<InputStream>> glusterPath(FileEntry fileEntry) {
        return glusterFileService.findFileForGet(fileEntry).
                andOnSuccess(fileAndStorageLocation -> glusterFileService.getInputStreamSupplier(fileAndStorageLocation.getKey()));
    }

    @Override
    public ServiceResult<FileEntry> deleteFileIgnoreNotFound(long fileEntryId) {
        fileServiceTransactionHelper.delete(fileEntryId);
        // TODO Async call to storage service to delete file - not that fussed though we can leave it in there
        // Why would you return the entity after delete? return new to fit interface
        return serviceSuccess(new FileEntry());
    }

}
