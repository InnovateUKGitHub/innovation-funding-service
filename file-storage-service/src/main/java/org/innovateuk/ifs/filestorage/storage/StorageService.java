package org.innovateuk.ifs.filestorage.storage;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.api.filestorage.v1.download.FileDownloadResponse;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadResponse;
import org.innovateuk.ifs.filestorage.repository.FileStorageRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class StorageService {

    @Autowired
    private FileStorageRecordRepository fileStorageRecordRepository;

    @Autowired
    private List<ReadableStorageProvider> readableStorageProviders;

    @Autowired
    private WritableStorageProvider writableStorageProvider;

    public FileUploadResponse fileUpload(FileUploadRequest fileUploadRequest) throws IOException {
        // add db entry
        // virus check
        // update virus details in db
        // store with provider
        // update details of storage in db
        // return response
        return null;//writableStorageProvider.saveFile(fileUploadRequest);
    }

    public Optional<FileDownloadResponse> fileByUuid(String uuid) throws IOException {
        // support multiple sources until migration completes
        for (ReadableStorageProvider storageProvider : readableStorageProviders) {
            if (storageProvider.fileExists(uuid)) {

                return null;//Optional.of(storageProvider.readFile(uuid));
            }
        }
        return Optional.empty();
    }

}
