package org.innovateuk.ifs.filestorage.storage.s3;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.filestorage.cfg.storage.BackingStoreConfiguration;
import org.innovateuk.ifs.filestorage.cfg.storage.BackingStoreConfigurationProperties;
import org.innovateuk.ifs.filestorage.util.TestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@Disabled("Just for dev - this requires s3 auth and should be integration level")
@Import(BackingStoreConfiguration.class)
@ActiveProfiles({IfsProfileConstants.S3_STORAGE})
class S3StorageProviderTest {

    @Autowired
    private S3StorageProvider s3StorageProvider;

    @Autowired
    private BackingStoreConfigurationProperties backingProps;

    @Test
    void readFile() {
        String uuid = UUID.randomUUID().toString();
        AmazonS3Exception ex = Assertions.assertThrows(AmazonS3Exception.class,
                () -> s3StorageProvider.readFile(uuid));
        assertThat(ex.getMessage().contains("The specified key does not exist"), equalTo(true));
    }

    @Test
    void fileExists() {
        assertThat(s3StorageProvider.fileExists(UUID.randomUUID().toString()), equalTo(false));
    }

    @Test
    void saveFile() throws IOException {
        UUID uuid = UUID.randomUUID();
        FileUploadRequest fileUploadRequest = TestHelper.build(uuid);
        String storageLocation = s3StorageProvider.saveFile(fileUploadRequest);
        assertThat(storageLocation, equalTo("https://"
                + backingProps.getS3().getFileStoreS3Bucket() + ".s3." + backingProps.getS3().getAwsRegion() + ".amazonaws.com/" + uuid));
        assertThat(s3StorageProvider.fileExists(uuid.toString()), equalTo(true));
        byte[] payload = s3StorageProvider.readFile(uuid.toString()).get();
        assertThat(payload, equalTo(fileUploadRequest.payload()));

    }
}