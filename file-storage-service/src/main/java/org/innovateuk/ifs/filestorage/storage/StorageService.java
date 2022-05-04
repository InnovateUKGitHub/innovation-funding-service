package org.innovateuk.ifs.filestorage.storage;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.api.filestorage.util.FileUploadRequestBuilder;
import org.innovateuk.ifs.api.filestorage.v1.download.FileDownloadResponse;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadResponse;
import org.innovateuk.ifs.filestorage.repository.FileStorageRecord;
import org.innovateuk.ifs.filestorage.repository.FileStorageRecordMapper;
import org.innovateuk.ifs.filestorage.repository.FileStorageRecordRepository;
import org.innovateuk.ifs.filestorage.util.FileUploadResponseMapper;
import org.innovateuk.ifs.filestorage.virusscan.VirusScanProvider;
import org.innovateuk.ifs.filestorage.virusscan.VirusScanResult;
import org.innovateuk.ifs.filestorage.web.StorageDownloadController;
import org.innovateuk.ifs.filestorage.web.StorageUploadController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
public class StorageService {

    @Autowired
    private FileStorageRecordRepository fileStorageRecordRepository;

    @Autowired
    private List<ReadableStorageProvider> readableStorageProviders;

    @Autowired
    private WritableStorageProvider writableStorageProvider;

    @Autowired
    private VirusScanProvider virusScanProvider;

    @Autowired
    private StorageServiceHelper storageServiceHelper;

    @Autowired
    private StorageUploadController storageUploadController;

    @Autowired
    private StorageDownloadController storageDownloadController;

    @Scheduled(fixedDelay = 2000)
    public void scheduled() throws IOException {
        log.error("sdfdfdfsdsfdfdfsdsf");
        storageUploadController.fileUpload(FileUploadRequestBuilder.fromResource(new ClassPathResource("test.txt"), MediaType.TEXT_PLAIN).userId("123").build());
    }

    public FileUploadResponse fileUpload(FileUploadRequest fileUploadRequest) throws IOException {
        FileStorageRecord fileStorageRecord = storageServiceHelper.saveInitialRequest(fileUploadRequest, writableStorageProvider);
        VirusScanResult virusScanResult = virusScanProvider.scanFile(fileUploadRequest.payload());
        fileStorageRecord = storageServiceHelper.updateVirusCheckStatus(fileStorageRecord, virusScanResult);
        String providerLocation = writableStorageProvider.saveFile(fileUploadRequest);
        storageServiceHelper.saveProviderResult(fileStorageRecord, providerLocation);
        return FileUploadResponseMapper.build(fileUploadRequest, virusScanResult);
    }

    public Optional<FileDownloadResponse> fileByUuid(String uuid) throws IOException {
        // support multiple sources until migration completes
        for (ReadableStorageProvider storageProvider : readableStorageProviders) {
            if (storageProvider.fileExists(uuid)) {
                Optional<FileStorageRecord> fileStorageRecord = fileStorageRecordRepository.findById(uuid);
                Optional<byte[]> payload = storageProvider.readFile(uuid);
                if (fileStorageRecord.isPresent() && payload.isPresent()) {
                    return Optional.of(FileStorageRecordMapper.from(fileStorageRecord.get(), payload.get()));
                }
            }
        }
        return Optional.empty();
    }

}
