package org.innovateuk.ifs.filestorage.storage;

import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.api.filestorage.v1.download.FileDownloadResponse;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.filestorage.cfg.StorageServiceConfiguration;
import org.innovateuk.ifs.filestorage.cfg.storage.BackingStoreConfigurationProperties;
import org.innovateuk.ifs.filestorage.repository.FileStorageRecord;
import org.innovateuk.ifs.filestorage.repository.FileStorageRecordRepository;
import org.innovateuk.ifs.filestorage.storage.local.LocalStorageProvider;
import org.innovateuk.ifs.filestorage.util.TestHelper;
import org.innovateuk.ifs.filestorage.web.StorageDownloadController;
import org.innovateuk.ifs.filestorage.web.StorageUploadController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@EnableAutoConfiguration
@SpringBootTest(classes = FileStorageRecordRepository.class)
@ActiveProfiles({IfsProfileConstants.LOCAL_STORAGE, IfsProfileConstants.STUB_AV_SCAN, IfsProfileConstants.TEST})
@EnableJpaRepositories(basePackageClasses = {FileStorageRecordRepository.class})
@EntityScan(basePackageClasses = {FileStorageRecord.class})
@Import(StorageServiceConfiguration.class)
class StorageServiceTest {

    @Autowired
    private StorageService storageService;

    @Autowired
    private FileStorageRecordRepository fileStorageRecordRepository;

    @Test
    void fileUpload() throws IOException {
        UUID uuid = UUID.randomUUID();
        FileUploadRequest fileUploadRequest = TestHelper.build(uuid);
        storageService.fileUpload(fileUploadRequest);
        FileStorageRecord fileStorageRecord = fileStorageRecordRepository.findById(uuid.toString()).get();
        assertThat(fileStorageRecord.fileUuid(), equalTo(uuid.toString()));
        FileDownloadResponse fileDownloadResponse = storageService.fileByUuid(uuid.toString()).get();
        assertThat(fileDownloadResponse.getFileId(), equalTo(uuid.toString()));
    }

}