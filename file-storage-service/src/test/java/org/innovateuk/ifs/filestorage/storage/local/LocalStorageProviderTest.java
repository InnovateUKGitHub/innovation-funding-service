package org.innovateuk.ifs.filestorage.storage.local;

import com.google.common.hash.Hashing;
import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.api.filestorage.util.FileUploadRequestBuilder;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.filestorage.cfg.storage.BackingStoreConfigurationProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
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

    private static final String TEST_IMAGE = "test.jpg";

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
        UUID uuid = UUID.randomUUID();
        FileUploadRequest.FileUploadRequestBuilder builder
                = FileUploadRequestBuilder.fromResource(uuid, new ClassPathResource(TEST_IMAGE), MediaType.IMAGE_JPEG);
        FileUploadRequest fileUploadRequest = builder.userId(LocalStorageProviderTest.class.getSimpleName()).build();
        String path = localStorageProvider.saveFile(fileUploadRequest);
        assertThat(path, equalTo(backingStoreConfigurationProperties.getLocalStorage().getRootFolderPath() + "/" + uuid));

        assertThat(localStorageProvider.fileExists(uuid.toString()), equalTo(true));

        Optional<byte[]> payload = localStorageProvider.readFile(uuid.toString());
        assertThat(payload.isPresent(), equalTo(true));
        assertThat(Hashing.sha256().hashBytes(payload.get()).toString(), equalTo(fileUploadRequest.checksum()));
    }
}