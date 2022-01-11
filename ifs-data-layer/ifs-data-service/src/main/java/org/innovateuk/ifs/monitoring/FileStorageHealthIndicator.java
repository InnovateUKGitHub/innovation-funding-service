package org.innovateuk.ifs.monitoring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * this class supplies the /health endpoint with the knowledge about if the file storage cluster is accessible.
 */
@Component
@Slf4j
public class FileStorageHealthIndicator implements HealthIndicator {

    @Value("${ifs.data.service.file.storage.base}")
    private String fileStoragePath;

    @Value("${ifs.data.service.file.storage.create:false}")
    private boolean allowCreateStoragePath;

    private FileOperationsWrapper fileOperationsWrapper = new FileOperationsWrapper();

    public void setFileOperationsWrapper(FileOperationsWrapper fileOperationsWrapper) {
        this.fileOperationsWrapper = fileOperationsWrapper;
    }

    public void setFileStoragePath(String fileStoragePath) {
        this.fileStoragePath = fileStoragePath;
    }

    public void setAllowCreateStoragePath(boolean allowCreateStoragePath) {
        this.allowCreateStoragePath = allowCreateStoragePath;
    }

    @Override
    public Health health() {
        log.debug("checking filesystem health");

        if (allowCreateStoragePath) {
            createStoragePathIfNotExist(fileStoragePath);
        }
        return createStatus(fileStoragePath).build();
    }

    private Health.Builder createStatus(final String storagePath) {
        Health.Builder builder = new Health.Builder();

        boolean isWritable = fileOperationsWrapper.isWritable(storagePath);

        if (isWritable) {
            log.debug("storage path [" + fileStoragePath + "] is writable");

            return builder.up();
        }

        log.debug("storage path [" + fileStoragePath + "] is not writable");

        return builder.down();
    }

    private void createStoragePathIfNotExist(final String storagePath) {

        boolean pathExists = fileOperationsWrapper.exists(storagePath);

        if (!pathExists) {
            fileOperationsWrapper.createDirectory(storagePath);
        }
    }

    static class FileOperationsWrapper {

        public boolean isWritable(String location) {
            Path filePath = FileSystems.getDefault().getPath(location);
            return Files.isWritable(filePath);
        }

        public boolean exists(String location) {
            Path filePath = FileSystems.getDefault().getPath(location);
            return filePath.toFile().exists();
        }

        public void createDirectory(String location) {
            Path filePath = FileSystems.getDefault().getPath(location);

            try {
                log.debug("trying to create directory");
                Files.createDirectory(filePath);
                log.debug("directory created");
            } catch (IOException e) {
                log.debug(e.getMessage(), e);
            }
        }
    }
}
