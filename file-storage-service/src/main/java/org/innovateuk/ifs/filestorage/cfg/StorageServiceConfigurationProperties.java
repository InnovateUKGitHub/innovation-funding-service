package org.innovateuk.ifs.filestorage.cfg;

import lombok.Getter;
import lombok.Setter;
import org.apache.tika.Tika;
import org.innovateuk.ifs.filestorage.cfg.storage.BackingStoreConfiguration;
import org.innovateuk.ifs.filestorage.cfg.virusscan.VirusScanConfiguration;
import org.innovateuk.ifs.filestorage.storage.StorageService;
import org.innovateuk.ifs.filestorage.storage.StorageServiceHelper;
import org.innovateuk.ifs.filestorage.storage.tika.TikaFileValidator;
import org.innovateuk.ifs.filestorage.web.StorageDownloadController;
import org.innovateuk.ifs.filestorage.web.StorageUploadController;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.innovateuk.ifs.filestorage.cfg.StorageServiceConfigurationProperties.STORAGE_SERVICE_CONFIG_PREFIX;
import static org.innovateuk.ifs.filestorage.cfg.storage.BackingStoreConfigurationProperties.BACKING_STORE_CONFIG_PREFIX;

@Getter
@Setter
@ConfigurationProperties(prefix = STORAGE_SERVICE_CONFIG_PREFIX)
public class StorageServiceConfigurationProperties {

    public static final String STORAGE_SERVICE_CONFIG_PREFIX = "ifs.filestorage";

    private boolean isMimeCheckEnabled = true;


}
