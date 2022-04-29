package org.innovateuk.ifs.filestorage.virusscan.clam;

import fi.solita.clamav.ClamAVClient;
import org.innovateuk.ifs.api.filestorage.v1.upload.VirusScanStatus;
import org.innovateuk.ifs.filestorage.cfg.virusscan.VirusScanConfigurationProperties;
import org.innovateuk.ifs.filestorage.virusscan.VirusScanProvider;
import org.innovateuk.ifs.filestorage.virusscan.VirusScanResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ClamAvScanProvider implements VirusScanProvider {

    @Autowired
    private VirusScanConfigurationProperties virusScanConfigurationProperties;

    public VirusScanResult scanFile(byte[] fileBytes) throws IOException {
        ClamAVClient clamAVClient = new ClamAVClient(
            virusScanConfigurationProperties.getClamAv().getHost(),
                virusScanConfigurationProperties.getClamAv().getPort(),
                    virusScanConfigurationProperties.getClamAv().getTimeout()
            );
        return mapResult(clamAVClient.scan(fileBytes));
    }

    private VirusScanResult mapResult(byte[] scanResult) {
        if (ClamAVClient.isCleanReply(scanResult)) {
            return new VirusScanResult(VirusScanStatus.VIRUS_FREE, new String(scanResult, StandardCharsets.US_ASCII));
        }
        return new VirusScanResult(VirusScanStatus.VIRUS_DETECTED, new String(scanResult, StandardCharsets.US_ASCII));
    }

}
