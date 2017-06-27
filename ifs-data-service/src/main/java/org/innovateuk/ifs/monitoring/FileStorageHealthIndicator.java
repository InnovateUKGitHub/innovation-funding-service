package org.innovateuk.ifs.monitoring;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * this class supplies the /health endpoint with the knowledge about if the file storage cluster is accessible.
 */
@Component
public class FileStorageHealthIndicator implements HealthIndicator {
    private static final Log LOG = org.apache.commons.logging.LogFactory.getLog(FileStorageHealthIndicator.class);

    @Value("${ifs.data.service.file.storage.base}")
    private String fileStoragePath;

    @Override public Health health() {
        LOG.info("checking filesystem Health");
        Path storagePath = FileSystems.getDefault().getPath(fileStoragePath);
        createStoragePathIfNotExist(storagePath);

        return createStatus(storagePath).build();
    }

    private Health.Builder createStatus(final Path storagePath) {
        Health.Builder builder = new Health.Builder();
        return Files.isWritable(storagePath) ? builder.up() : builder.down();
    }

    private void createStoragePathIfNotExist(final Path storagePath) {
        if(!storagePath.toFile().exists()) {
            try {
                LOG.debug("trying to create directory");
                Files.createDirectory(storagePath);
                LOG.debug("directory created");
            } catch (IOException e) {
                LOG.debug(e);
            }
        }
    }
}
