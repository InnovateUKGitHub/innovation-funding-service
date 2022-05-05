package org.innovateuk.ifs.filestorage.storage.local;

import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.api.filestorage.util.FileHashing;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.filestorage.cfg.storage.BackingStoreConfigurationProperties;
import org.innovateuk.ifs.filestorage.util.TestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(classes = {LocalStorageProvider.class})
@ActiveProfiles({IfsProfileConstants.LOCAL_STORAGE})
@EnableConfigurationProperties(BackingStoreConfigurationProperties.class)
class LocalStorageProviderTest {


    @Autowired
    private LocalStorageProvider localStorageProvider;

    @Autowired
    private BackingStoreConfigurationProperties backingStoreConfigurationProperties;

    @Test
    void readFileFail() throws IOException {
        assertThat(localStorageProvider.readFile(UUID.randomUUID().toString()).isPresent(), equalTo(false));
    }

    @Test
    void fileExistsFail() throws IOException {
        assertThat(localStorageProvider.fileExists(UUID.randomUUID().toString()), equalTo(false));
    }

    @Test
    void saveFile() throws IOException {
        FileUploadRequest fileUploadRequest = TestHelper.build();
        String path = localStorageProvider.saveFile(fileUploadRequest);

        assertThat(path, equalTo(backingStoreConfigurationProperties.getLocalStorage().getRootFolderPath()
                + "/" + fileUploadRequest.getFileId().toString()));

        assertThat(localStorageProvider.fileExists(fileUploadRequest.getFileId().toString()), equalTo(true));

        Optional<byte[]> payload = localStorageProvider.readFile(fileUploadRequest.getFileId().toString());
        assertThat(payload.isPresent(), equalTo(true));
        assertThat(FileHashing.fileHash64(payload.get()), equalTo(fileUploadRequest.getMd5Checksum()));
    }
}