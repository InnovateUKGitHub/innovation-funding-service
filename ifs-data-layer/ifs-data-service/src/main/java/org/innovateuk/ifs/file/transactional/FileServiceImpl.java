package org.innovateuk.ifs.file.transactional;

import com.google.common.io.ByteStreams;
import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.api.filestorage.util.FileUploadRequestBuilder;
import org.innovateuk.ifs.api.filestorage.v1.download.FileDownload;
import org.innovateuk.ifs.api.filestorage.v1.download.FileDownloadResponse;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUpload;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadResponse;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.repository.FileEntryRepository;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileService;
import org.innovateuk.ifs.file.transactional.gluster.GlusterFileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;
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
    private FileEntryRepository fileEntryRepository;

    @Autowired
    private FileDownload fileDownloadFeign;

    @Autowired
    private FileUpload fileUploadFeign;

    @Autowired
    private GlusterFileServiceImpl glusterFileService;

    @Override
    @Transactional
    public ServiceResult<FileEntry> createFile(FileEntryResource resource, Supplier<InputStream> inputStreamSupplier) {
        UUID uuid = UUID.randomUUID();
        FileEntry fileEntry = new FileEntry();
        fileEntry.setFileUuid(uuid.toString());
        fileEntryRepository.save(fileEntry);

        FileUploadRequest fileUploadRequest = null;
        try {
            fileUploadRequest = FileUploadRequestBuilder.fromResource(new InputStreamResource(inputStreamSupplier.get()),
                    MediaType.valueOf(resource.getMediaType()), "IFS").fileName(resource.getName()).build();
        } catch (IOException e) {
            return ServiceResult.serviceFailure(new Error(FILES_UNABLE_TO_CREATE_FILE));
        }

        try {
            ResponseEntity<FileUploadResponse> fileUploadResponse = fileUploadFeign.fileUpload(fileUploadRequest);
            fileEntry.setMd5Checksum(fileUploadResponse.getBody().getMd5Checksum());
            return serviceSuccess(fileEntry);
        } catch (ResponseStatusException responseStatusException) {
            // TODO some sort of adaptor to map from this to CommonFailureKeys
            return serviceFailure(responseStatusException);
        }
    }

    @Override
    public ServiceResult<Supplier<InputStream>> getFileByFileEntryId(Long fileEntryId) {
        Optional<FileEntry> fileEntry = fileEntryRepository.findById(fileEntryId);
        if (!fileEntry.isPresent()) {
            return serviceFailure(new Error(FILES_UNABLE_TO_FIND_FILE_ENTRY_ID_FROM_FILE));
        }
        if (fileEntry.get().getFileUuid() != null) {
            // get file from file upload service
            return obtainFromStorageService(fileEntry.get().getFileUuid());
        }
        // old path via gluster - see gluster package
        return glusterFileService.findFileForGet(fileEntry.get()).
                andOnSuccess(fileAndStorageLocation -> glusterFileService.getInputStreamSupplier(fileAndStorageLocation.getKey()));
    }

    private ServiceResult<Supplier<InputStream>> obtainFromStorageService(String fileUuid) {
        ResponseEntity<FileDownloadResponse> fileDownloadResponse = fileDownloadFeign.fileDownloadResponse(fileUuid);
        if (fileDownloadResponse.getStatusCode().is2xxSuccessful()) {
            return serviceSuccess(() -> new ByteArrayInputStream(fileDownloadResponse.getBody().getPayload()));
        }
        return serviceFailure(new Error("File Storage Service Error", fileDownloadResponse.getStatusCode()));
    }

    @Override
    @Transactional
    public ServiceResult<FileEntry> updateFile(FileEntryResource fileToUpdate, Supplier<InputStream> inputStreamSupplier) {
        UUID uuid = UUID.randomUUID();

        ServiceResult serviceResult = validateContentLength(fileToUpdate.getFilesizeBytes(), inputStreamSupplier);
        if (serviceResult.isFailure()) {
            return serviceResult;
        }
        FileEntry fileEntry = fileEntryRepository.findById(fileToUpdate.getId()).get();
        fileEntry.setFileUuid(uuid.toString());
        fileEntryRepository.save(fileEntry);

        FileUploadRequest fileUploadRequest = null;
        try {
            fileUploadRequest = FileUploadRequestBuilder.fromResource(new InputStreamResource(inputStreamSupplier.get()), MediaType.valueOf(fileToUpdate.getMediaType()), "IFS").fileName(fileToUpdate.getName()).build();
        } catch (IOException e) {
            return ServiceResult.serviceFailure(new Error(FILES_UNABLE_TO_CREATE_FILE));
        }

        ResponseEntity<FileUploadResponse> fileUploadResponse = fileUploadFeign.fileUpload(fileUploadRequest);
        if (fileUploadResponse.getStatusCode().is2xxSuccessful()) {
            fileEntry.setMd5Checksum(fileUploadResponse.getBody().getMd5Checksum());
            return serviceSuccess(fileEntry);
        }
        return serviceFailure(new Error(FILES_UNABLE_TO_CREATE_FILE));
    }

    /**
     * This method is preferred over deleteFile method above to avoid blocking removal of files when they are missing.
     * There have been issues on production environment resulting in missing files.  This meant users were not able
     * to reupload after removing files.  See IFS-955.
     * Such records where files are actually missing will be logged as an error but file entry record will be removed
     * and no exception thrown.  So re-upload is allowed.
     * @param fileEntryId
     * @return
     */
    @Override
    @Transactional
    public ServiceResult<FileEntry> deleteFileIgnoreNotFound(long fileEntryId) {
        Optional<FileEntry> fileEntry = fileEntryRepository.findById(fileEntryId);
        fileEntryRepository.delete(fileEntry.get());
        // TODO Async call to storage service to delete file - not that fussed though we can leave it in there
        return serviceSuccess(fileEntry.get());
    }


    // TODO move me to FSS
    private ServiceResult<InputStream> validateContentLength(long filesizeBytes, Supplier<InputStream> inputStream) {
        byte[] targetArray;
        try {
            targetArray = ByteStreams.toByteArray(inputStream.get());
        } catch (IOException e) {
            return serviceFailure(new Error(FILES_INCORRECTLY_REPORTED_FILESIZE, "Failed to read stream"));
        }
        if (targetArray.length == filesizeBytes) {
            return serviceSuccess(inputStream.get());
        } else {
            log.error("Reported filesize was " + filesizeBytes + " bytes but actual file is " + targetArray.length + " bytes");
            return serviceFailure(new Error(FILES_INCORRECTLY_REPORTED_FILESIZE, targetArray.length));
        }
    }

}
