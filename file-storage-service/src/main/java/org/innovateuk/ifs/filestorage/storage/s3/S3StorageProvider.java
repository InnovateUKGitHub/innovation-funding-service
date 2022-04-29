package org.innovateuk.ifs.filestorage.storage.s3;

import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.filestorage.cfg.FileStorageConfigurationProperties;
import org.innovateuk.ifs.filestorage.storage.ReadableStorageProvider;
import org.innovateuk.ifs.filestorage.storage.WritableStorageProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Optional;

public class S3StorageProvider implements ReadableStorageProvider, WritableStorageProvider {

    @Autowired
    private FileStorageConfigurationProperties fileStorageConfigurationProperties;


    @Override
    public Optional<byte[]> readFile(String uuid) throws IOException {
        return Optional.empty();
    }

    @Override
    public boolean fileExists(String uuid) throws IOException {
        return false;
    }

    @Override
    public String saveFile(FileUploadRequest fileUploadRequest) throws IOException {
        return null;
    }
}
