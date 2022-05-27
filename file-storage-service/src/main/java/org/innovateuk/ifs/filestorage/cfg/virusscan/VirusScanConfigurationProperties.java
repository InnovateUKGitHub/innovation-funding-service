package org.innovateuk.ifs.filestorage.cfg.virusscan;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static org.innovateuk.ifs.filestorage.cfg.virusscan.VirusScanConfigurationProperties.VIRUS_SCAN_CONFIG_PREFIX;

@Getter
@Setter
@ConfigurationProperties(prefix = VIRUS_SCAN_CONFIG_PREFIX)
public class VirusScanConfigurationProperties {

    public static final String VIRUS_SCAN_CONFIG_PREFIX = "ifs.filestorage.virusscan";

    private final ClamAv clamAv = new ClamAv();

    @Getter
    @Setter
    public static class ClamAv {

        private String host;

        private int port;

        private int clientTimeoutMs;

        private int scanChunkSizeBytes;

    }
}
