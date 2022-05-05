package org.innovateuk.ifs.filestorage.storage;

import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.filestorage.repository.FileStorageRecord;
import org.innovateuk.ifs.filestorage.repository.FileStorageRecordMapper;
import org.innovateuk.ifs.filestorage.repository.FileStorageRecordRepository;
import org.innovateuk.ifs.filestorage.virusscan.VirusScanResult;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;

public class StorageServiceHelper {

    @Autowired
    private FileStorageRecordRepository fileStorageRecordRepository;

    @Transactional
    public FileStorageRecord saveInitialRequest(FileUploadRequest fileUploadRequest, WritableStorageProvider writableStorageProvider) {
        return fileStorageRecordRepository.save(FileStorageRecordMapper.to(fileUploadRequest, writableStorageProvider));
    }

    @Transactional
    public FileStorageRecord updateVirusCheckStatus(FileStorageRecord fileStorageRecord, VirusScanResult virusScanResult) {
        fileStorageRecord.virusScanStatus(virusScanResult.virusScanStatus());
        fileStorageRecord.virusScanMessage(virusScanResult.virusScanResultMessage());
        return fileStorageRecordRepository.save(fileStorageRecord);
    }

    @Transactional
    public FileStorageRecord saveProviderResult(FileStorageRecord fileStorageRecord, String providerStorageLocation) {
        fileStorageRecord.storageLocation(providerStorageLocation);
        return fileStorageRecordRepository.save(fileStorageRecord);
    }
}
