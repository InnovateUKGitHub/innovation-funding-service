package org.innovateuk.ifs.filestorage.cfg;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = FileStorageConfigurationProperties.FILE_STORAGE_CONFIG_PREFIX)
public class FileStorageConfigurationProperties {

    public static final String FILE_STORAGE_CONFIG_PREFIX = "ifs.filestorage";

}
