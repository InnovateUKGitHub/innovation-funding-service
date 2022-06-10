package org.innovateuk.ifs.filestorage.storage;

import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.filestorage.repository.FileStorageRecord;
import org.innovateuk.ifs.filestorage.repository.FileStorageRecordMapper;
import org.innovateuk.ifs.filestorage.repository.FileStorageRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;

public class StorageServiceHelper {

    @Autowired
    private FileStorageRecordRepository fileStorageRecordRepository;


    @Transactional
    public FileStorageRecord saveProviderResult(FileUploadRequest fileUploadRequest, String providerStorageLocation) {
        return fileStorageRecordRepository.save(FileStorageRecordMapper.to(fileUploadRequest, providerStorageLocation));
    }

    @Transactional
    public FileStorageRecord saveErrorResult(FileUploadRequest fileUploadRequest, ResponseStatusException ex) {
        return fileStorageRecordRepository.save(FileStorageRecordMapper.fromError(fileUploadRequest, ex));
    }
}
