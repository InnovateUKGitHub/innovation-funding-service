package org.innovateuk.ifs.filestorage.storage.local;

import com.google.common.io.Files;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.filestorage.cfg.storage.BackingStoreConfigurationProperties;
import org.innovateuk.ifs.filestorage.storage.ReadableStorageProvider;
import org.innovateuk.ifs.filestorage.storage.WritableStorageProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

/**
 * This is a basic version of a local storage provider for dev use and local testing where
 * gluster or s3 are not available.
 */
public class LocalStorageProvider implements ReadableStorageProvider, WritableStorageProvider {

    @Autowired
    private BackingStoreConfigurationProperties backingStoreConfigurationProperties;

    @Override
    public Optional<byte[]> readFile(String uuid) throws IOException {
        File target = Path.of(backingStoreConfigurationProperties.getLocalStorage().getRootFolderPath(), uuid).toFile();
        if (target.exists()) {
            return Optional.of(Files.toByteArray(target));
        }
        return Optional.empty();
    }

    @Override
    public boolean fileExists(String uuid) {
        File file = Path.of(backingStoreConfigurationProperties.getLocalStorage().getRootFolderPath(), uuid).toFile();
        return file.exists();
    }

    @Override
    public String saveFile(FileUploadRequest fileUploadRequest) throws IOException {
        File target = Path.of(backingStoreConfigurationProperties.getLocalStorage().getRootFolderPath(),
                fileUploadRequest.fileId().toString()).toFile();
        Files.write(fileUploadRequest.payload(),
                Path.of(backingStoreConfigurationProperties.getLocalStorage().getRootFolderPath(),
                        fileUploadRequest.fileId().toString()).toFile());
        return target.getAbsolutePath();
    }
}
