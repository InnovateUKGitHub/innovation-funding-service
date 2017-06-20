package org.innovateuk.ifs.file.transactional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration around the storage of files in various locations
 */
@Configuration
public class FileStorageConfig {

    @Value("${ifs.data.service.file.storage.base}")
    private String pathToStorageBase;

    @Value("${ifs.data.service.file.storage.containing.folder}")
    private String fileStorageFolder;

    @Value("${ifs.data.service.file.storage.virus.scanning.holding.folder}")
    private String virusScanningHoldingFolder;

    @Value("${ifs.data.service.file.storage.virus.scanning.quarantine.folder}")
    private String virusScanningQuarantineFolder;

    @Value("${ifs.data.service.file.storage.virus.scanning.scanned.folder}")
    private String virusScanningScannedFolder;

    @Bean
    @Qualifier("temporaryHoldingFileStorageStrategy")
    public FileStorageStrategy getInitialStorageLocationStrategy() {
        return new FlatFolderFileStorageStrategy(pathToStorageBase, virusScanningHoldingFolder);
    }

    @Bean
    @Qualifier("quarantinedFileStorageStrategy")
    public FileStorageStrategy getVirusScanningQuarantinedFolderStrategy() {
        return new FlatFolderFileStorageStrategy(pathToStorageBase, virusScanningQuarantineFolder);
    }

    @Bean
    @Qualifier("scannedFileStorageStrategy")
    public FileStorageStrategy getVirusScanningScannedFolderStrategy() {
        return new FlatFolderFileStorageStrategy(pathToStorageBase, virusScanningScannedFolder);
    }

    @Bean
    @Qualifier("finalFileStorageStrategy")
    public FileStorageStrategy getFinalStorageLocationStrategy() {
        return new ByFileIdFileStorageStrategy(pathToStorageBase, fileStorageFolder);
    }
}
