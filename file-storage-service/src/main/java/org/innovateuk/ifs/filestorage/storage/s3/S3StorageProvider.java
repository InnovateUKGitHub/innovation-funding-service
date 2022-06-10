package org.innovateuk.ifs.filestorage.storage.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.api.filestorage.v1.delete.FileDeletionRequest;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.filestorage.cfg.storage.BackingStoreConfigurationProperties;
import org.innovateuk.ifs.filestorage.exception.ServiceException;
import org.innovateuk.ifs.filestorage.storage.ReadableStorageProvider;
import org.innovateuk.ifs.filestorage.storage.WritableStorageProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Optional;

@Slf4j
public class S3StorageProvider implements ReadableStorageProvider, WritableStorageProvider {

    @Autowired
    private BackingStoreConfigurationProperties backingConfig;

    @Autowired
    private AmazonS3 amazonS3;

    @Override
    public Optional<byte[]> readFile(String uuid) throws IOException {
        if (amazonS3.doesBucketExistV2(backingConfig.getS3().getFileStoreS3Bucket())) {
            S3Object s3Object = amazonS3.getObject(backingConfig.getS3().getFileStoreS3Bucket(), uuid);
            return Optional.of(ByteStreams.toByteArray(s3Object.getObjectContent()));
        }
        return Optional.empty();
    }

    @Override
    public boolean fileExists(String uuid) {
        if (amazonS3.doesBucketExistV2(backingConfig.getS3().getFileStoreS3Bucket())) {
            return amazonS3.doesObjectExist(backingConfig.getS3().getFileStoreS3Bucket(), uuid);
        }
        return false;
    }

    @Override
    public String saveFile(FileUploadRequest fileUploadRequest) throws ServiceException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileUploadRequest.getFileSizeBytes());
        objectMetadata.setContentType(fileUploadRequest.getMimeType());
        objectMetadata.setContentMD5(fileUploadRequest.getMd5Checksum());
        try {
            amazonS3.putObject(
                    backingConfig.getS3().getFileStoreS3Bucket(),
                    fileUploadRequest.getFileId(),
                    ByteSource.wrap(fileUploadRequest.getPayload()).openStream(),
                    objectMetadata
            );
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return amazonS3.getUrl(backingConfig.getS3().getFileStoreS3Bucket(), fileUploadRequest.getFileId()).toString();
    }

    @Override
    public String deleteFile(FileDeletionRequest fileDeletionRequest) {
        try {
            amazonS3.deleteObject(
                    backingConfig.getS3().getFileStoreS3Bucket(),
                    fileDeletionRequest.getFileId()
            );
            return fileDeletionRequest.getFileId();
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
}
