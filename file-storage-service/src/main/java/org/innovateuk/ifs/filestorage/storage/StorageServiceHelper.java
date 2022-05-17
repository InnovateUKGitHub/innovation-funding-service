package org.innovateuk.ifs.filestorage.storage;

import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.filestorage.repository.FileStorageRecord;
import org.innovateuk.ifs.filestorage.repository.FileStorageRecordMapper;
import org.innovateuk.ifs.filestorage.repository.FileStorageRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.io.IOException;

public class StorageServiceHelper {

    @Autowired
    private FileStorageRecordRepository fileStorageRecordRepository;

    @Autowired
    private FileStorageRecordMapper fileStorageRecordMapper;

    @Transactional
    public FileStorageRecord saveProviderResult(FileUploadRequest fileUploadRequest, String providerStorageLocation) {
        return fileStorageRecordRepository.save(fileStorageRecordMapper.to(fileUploadRequest, providerStorageLocation));
    }

    @Transactional
    public FileStorageRecord saveErrorResult(FileUploadRequest fileUploadRequest, Exception ex) {
        return fileStorageRecordRepository.save(fileStorageRecordMapper.fromError(fileUploadRequest, ex));
    }
}
