package org.innovateuk.ifs.filestorage.cfg;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static org.innovateuk.ifs.filestorage.cfg.StorageServiceConfigurationProperties.STORAGE_SERVICE_CONFIG_PREFIX;

@Getter
@Setter
@ConfigurationProperties(prefix = STORAGE_SERVICE_CONFIG_PREFIX)
public class StorageServiceConfigurationProperties {

    public static final String STORAGE_SERVICE_CONFIG_PREFIX = "ifs.filestorage";

    private boolean isMimeCheckEnabled = true;


}
