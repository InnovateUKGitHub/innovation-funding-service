package org.innovateuk.ifs.filestorage.cfg;

import org.innovateuk.ifs.filestorage.cfg.storage.BackingStoreConfiguration;
import org.innovateuk.ifs.filestorage.cfg.virusscan.VirusScanConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({BackingStoreConfiguration.class, VirusScanConfiguration.class})
public class StorageServiceConfiguration {
}
