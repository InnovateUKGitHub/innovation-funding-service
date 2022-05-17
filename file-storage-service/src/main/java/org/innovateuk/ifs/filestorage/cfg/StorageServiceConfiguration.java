package org.innovateuk.ifs.filestorage.cfg;

import org.apache.tika.Tika;
import org.innovateuk.ifs.filestorage.cfg.storage.BackingStoreConfiguration;
import org.innovateuk.ifs.filestorage.cfg.virusscan.VirusScanConfiguration;
import org.innovateuk.ifs.filestorage.storage.StorageService;
import org.innovateuk.ifs.filestorage.storage.StorageServiceHelper;
import org.innovateuk.ifs.filestorage.storage.tika.TikaFileValidator;
import org.innovateuk.ifs.filestorage.web.StorageDownloadController;
import org.innovateuk.ifs.filestorage.web.StorageUploadController;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(StorageServiceConfigurationProperties.class)
@Import({BackingStoreConfiguration.class, VirusScanConfiguration.class})
public class StorageServiceConfiguration {

    @Bean
    public StorageService storageService() {
        return new StorageService();
    }

    @Bean
    public StorageServiceHelper storageServiceHelper() {
        return new StorageServiceHelper();
    }

    @Bean
    public StorageUploadController storageUploadController() {
        return new StorageUploadController();
    }

    @Bean
    public StorageDownloadController storageDownloadController() {
        return new StorageDownloadController();
    }

    @Bean
    public Tika tika() { return new Tika(); }

    @Bean
    public TikaFileValidator tikaFileValidator() { return new TikaFileValidator(); }
}
