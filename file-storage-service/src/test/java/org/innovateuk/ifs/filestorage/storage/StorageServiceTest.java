package org.innovateuk.ifs.filestorage.storage;

import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.filestorage.cfg.storage.BackingStoreConfigurationProperties;
import org.innovateuk.ifs.filestorage.repository.FileStorageRecord;
import org.innovateuk.ifs.filestorage.repository.FileStorageRecordRepository;
import org.innovateuk.ifs.filestorage.storage.local.LocalStorageProvider;
import org.innovateuk.ifs.filestorage.web.StorageDownloadController;
import org.innovateuk.ifs.filestorage.web.StorageUploadController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@EnableAutoConfiguration
@SpringBootTest(classes = {LocalStorageProvider.class, StorageService.class, StorageUploadController.class, StorageDownloadController.class})
@ActiveProfiles({IfsProfileConstants.LOCAL_STORAGE, IfsProfileConstants.TEST})
@EnableJpaRepositories(basePackageClasses = {FileStorageRecordRepository.class})
@EntityScan(basePackageClasses = {FileStorageRecord.class})
@EnableConfigurationProperties(BackingStoreConfigurationProperties.class)
class StorageServiceTest {

    @Test
    void fileUpload() {
    }

    @Test
    void fileByUuid() {
    }
}