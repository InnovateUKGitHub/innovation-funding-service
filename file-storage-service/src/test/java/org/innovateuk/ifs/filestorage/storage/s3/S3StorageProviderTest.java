package org.innovateuk.ifs.filestorage.storage.s3;

import com.amazonaws.DefaultRequest;
import com.amazonaws.Request;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.internal.Constants;
import com.amazonaws.services.s3.internal.ServiceUtils;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.S3Object;
import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.filestorage.cfg.storage.BackingStoreConfiguration;
import org.innovateuk.ifs.filestorage.cfg.storage.BackingStoreConfigurationProperties;
import org.innovateuk.ifs.filestorage.util.TestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles(IfsProfileConstants.TEST)
@SpringBootTest(classes = {S3StorageProvider.class})
@EnableConfigurationProperties(BackingStoreConfigurationProperties.class)
@Execution(ExecutionMode.SAME_THREAD)
class S3StorageProviderTest {

    @Autowired
    private S3StorageProvider s3StorageProvider;

    @Autowired
    private BackingStoreConfigurationProperties backingProps;

    @MockBean
    private AmazonS3 amazonS3;

    @Test
    void readFileFail() {
        String uuid = UUID.randomUUID().toString();
        when(amazonS3.doesBucketExistV2(backingProps.getS3().getFileStoreS3Bucket())).thenReturn(true);
        when(amazonS3.getObject(backingProps.getS3().getFileStoreS3Bucket(), uuid)).thenThrow(AmazonS3Exception.class);
        AmazonS3Exception ex = Assertions.assertThrows(AmazonS3Exception.class, () -> s3StorageProvider.readFile(uuid));
    }

    @Test
    void readFile() throws IOException {
        String uuid = UUID.randomUUID().toString();
        when(amazonS3.doesBucketExistV2(backingProps.getS3().getFileStoreS3Bucket())).thenReturn(true);
        S3Object s3Object = new S3Object();
        s3Object.setObjectContent(new ByteArrayInputStream("foo".getBytes(StandardCharsets.UTF_8)));
        when(amazonS3.getObject(backingProps.getS3().getFileStoreS3Bucket(), uuid)).thenReturn(s3Object);
        assertThat(s3StorageProvider.readFile(uuid).get(), equalTo("foo".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void fileExists() {
        String uuid = UUID.randomUUID().toString();
        when(amazonS3.doesBucketExistV2(backingProps.getS3().getFileStoreS3Bucket())).thenReturn(false);
        when(amazonS3.doesObjectExist(backingProps.getS3().getFileStoreS3Bucket(), uuid)).thenReturn(true);
        assertThat(s3StorageProvider.fileExists(uuid), equalTo(false));
        when(amazonS3.doesBucketExistV2(backingProps.getS3().getFileStoreS3Bucket())).thenReturn(true);
        assertThat(s3StorageProvider.fileExists(uuid), equalTo(true));
    }

    @Test
    void saveFile() throws IOException {
        UUID uuid = UUID.randomUUID();
        FileUploadRequest fileUploadRequest = TestHelper.build(uuid);
        when(amazonS3.getUrl(backingProps.getS3().getFileStoreS3Bucket(), uuid.toString())).thenReturn(new URL("https://"
            + backingProps.getS3().getFileStoreS3Bucket() + ".s3." + backingProps.getS3().getAwsRegion() + ".amazonaws.com/" + uuid));
        String storageLocation = s3StorageProvider.saveFile(fileUploadRequest);
        assertThat(storageLocation, equalTo("https://"
                + backingProps.getS3().getFileStoreS3Bucket() + ".s3." + backingProps.getS3().getAwsRegion() + ".amazonaws.com/" + uuid));
    }

}