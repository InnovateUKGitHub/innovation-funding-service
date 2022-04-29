package org.innovateuk.ifs.filestorage.storage.s3;

import com.amazonaws.services.s3.AmazonS3;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.filestorage.cfg.storage.BackingStoreConfigurationProperties;
import org.innovateuk.ifs.filestorage.storage.ReadableStorageProvider;
import org.innovateuk.ifs.filestorage.storage.WritableStorageProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Optional;

public class S3StorageProvider implements ReadableStorageProvider, WritableStorageProvider {

    @Autowired
    private BackingStoreConfigurationProperties backingConfig;

    @Autowired
    private AmazonS3 amazonS3;

    @Override
    public Optional<byte[]> readFile(String uuid) throws IOException {
        return Optional.empty();
    }

    @Override
    public boolean fileExists(String uuid) throws IOException {
        if (amazonS3.doesBucketExistV2(backingConfig.getS3().getFileStoreS3Bucket())) {
            return amazonS3.doesObjectExist(backingConfig.getS3().getFileStoreS3Bucket(), uuid);
        }
        return false;
    }

    @Override
    public String saveFile(FileUploadRequest fileUploadRequest) throws IOException {
        return null;
    }
}
