package org.innovateuk.ifs.filestorage.storage.local;

import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileDeletionRequest;
import org.innovateuk.ifs.api.filestorage.v1.upload.FileUploadRequest;
import org.innovateuk.ifs.filestorage.cfg.storage.BackingStoreConfigurationProperties;
import org.innovateuk.ifs.filestorage.exception.ServiceException;
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
@Slf4j
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
    public String saveFile(FileUploadRequest fileUploadRequest) {
        File target = Path.of(backingStoreConfigurationProperties.getLocalStorage().getRootFolderPath(),
                fileUploadRequest.getFileId()).toFile();
        try {
            Files.write(fileUploadRequest.getPayload(),
                    Path.of(backingStoreConfigurationProperties.getLocalStorage().getRootFolderPath(),
                            fileUploadRequest.getFileId()).toFile());
        } catch (IOException e) {
            throw new ServiceException(e);
        }
        return target.getAbsolutePath();
    }

    @Override
    public String deleteFile(FileDeletionRequest fileDeletionRequest) {
        Path target = Path.of(backingStoreConfigurationProperties.getLocalStorage().getRootFolderPath(),
                fileDeletionRequest.getFileId());
        try {
            java.nio.file.Files.delete(target);
        } catch (IOException e) {
            // local storage is dev only
            log.info(e.getMessage(), e);
        }
        return fileDeletionRequest.getFileId();
    }
}
