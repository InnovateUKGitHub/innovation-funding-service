package org.innovateuk.ifs.filestorage.storage;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.api.filestorage.v1.download.FileDownloadResponse;
import org.innovateuk.ifs.api.filestorage.v1.delete.FileDeletionRequest;
import org.innovateuk.ifs.api.filestorage.v1.delete.FileDeletionResponse;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadResponse;
import org.innovateuk.ifs.filestorage.cfg.StorageServiceConfigurationProperties;
import org.innovateuk.ifs.filestorage.exception.NoSuchRecordException;
import org.innovateuk.ifs.filestorage.exception.ServiceException;
import org.innovateuk.ifs.filestorage.exception.VirusDetectedException;
import org.innovateuk.ifs.filestorage.repository.FileStorageRecord;
import org.innovateuk.ifs.filestorage.repository.FileStorageRecordMapper;
import org.innovateuk.ifs.filestorage.repository.FileStorageRecordRepository;
import org.innovateuk.ifs.filestorage.storage.validator.TikaFileValidator;
import org.innovateuk.ifs.filestorage.storage.validator.UploadValidator;
import org.innovateuk.ifs.filestorage.util.FileUploadResponseMapper;
import org.innovateuk.ifs.filestorage.virusscan.VirusScanProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.InvalidMimeTypeException;
import org.springframework.util.StopWatch;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Optional;

@Slf4j
public class StorageService {

    @Autowired
    private StorageServiceConfigurationProperties storageServiceConfigurationProperties;

    @Autowired
    private FileStorageRecordRepository fileStorageRecordRepository;

    @Autowired
    private ReadableStorageProvider readableStorageProvider;

    @Autowired
    private WritableStorageProvider writableStorageProvider;

    @Autowired
    private VirusScanProvider virusScanProvider;

    @Autowired
    private StorageServiceHelper storageServiceHelper;

    @Autowired
    private TikaFileValidator tikaFileValidator;

    @Autowired
    private UploadValidator uploadValidator;

    public FileUploadResponse fileUpload(FileUploadRequest fileUploadRequest) throws VirusDetectedException, InvalidMimeTypeException {
        StopWatch stopWatch = new StopWatch(StorageService.class.getSimpleName()
                + " id: " + fileUploadRequest.getFileId()
                + " bytes: " + fileUploadRequest.getFileSizeBytes());

        try {
            stopWatch.start("Virus Scan");
            virusScanProvider.scanFile(fileUploadRequest.getPayload());
            stopWatch.stop();

            stopWatch.start("Validate Upload");
            uploadValidator.validateFile(fileUploadRequest);
            stopWatch.stop();

            if (storageServiceConfigurationProperties.isMimeCheckEnabled()) {
                stopWatch.start("Mime check with tika");
                tikaFileValidator.validatePayload(fileUploadRequest.getMimeType(), fileUploadRequest.getPayload(), fileUploadRequest.getFileName());
                stopWatch.stop();
            }

            stopWatch.start("Push to storage provider : " + writableStorageProvider.getClass().getSimpleName());
            String providerLocation = writableStorageProvider.saveFile(fileUploadRequest);
            stopWatch.stop();

            stopWatch.start("Update Stored Status in DB");
            storageServiceHelper.saveProviderResult(fileUploadRequest, providerLocation);
            stopWatch.stop();
            log.info(stopWatch.prettyPrint());
        } catch (ResponseStatusException responseStatusException) {
            stopWatch.stop();
            stopWatch.start("Update Failed Status in DB");
            storageServiceHelper.saveErrorResult(fileUploadRequest, responseStatusException);
            stopWatch.stop();
            log.info(stopWatch.prettyPrint());
            throw responseStatusException;
        }

        return FileUploadResponseMapper.build(fileUploadRequest);
    }

    public FileDownloadResponse fileByUuid(String uuid) throws NoSuchRecordException {
        if (readableStorageProvider.fileExists(uuid)) {
            Optional<FileStorageRecord> fileStorageRecord = fileStorageRecordRepository.findById(uuid);
            Optional<byte[]> payload;
            try {
                payload = readableStorageProvider.readFile(uuid);
            } catch (IOException e) {
                throw new ServiceException(e);
            }
            if (fileStorageRecord.isPresent() && payload.isPresent()) {
                return FileStorageRecordMapper.from(fileStorageRecord.get(), payload.get());
            }
        }
        throw new NoSuchRecordException(uuid);
    }

    public FileDeletionResponse deleteFile(FileDeletionRequest fileDeletionRequest) {
        return new FileDeletionResponse(writableStorageProvider.deleteFile(fileDeletionRequest));
    }
}
