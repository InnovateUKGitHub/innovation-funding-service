package org.innovateuk.ifs.filestorage.cfg;

import org.innovateuk.ifs.filestorage.cfg.storage.BackingStoreConfiguration;
import org.innovateuk.ifs.filestorage.cfg.virusscan.VirusScanConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(FileStorageConfigurationProperties.class)
@Import({VirusScanConfiguration.class, BackingStoreConfiguration.class})
public class FileStorageAutoConfiguration {
}
