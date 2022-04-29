package org.innovateuk.ifs.filestorage.virusscan.stub;

import org.innovateuk.ifs.api.filestorage.v1.upload.VirusScanStatus;
import org.innovateuk.ifs.filestorage.virusscan.VirusScanProvider;
import org.innovateuk.ifs.filestorage.virusscan.VirusScanResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class StubScanProvider implements VirusScanProvider {

    private static final byte[] EICAR
            = "X5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*".getBytes(StandardCharsets.UTF_8);

    @Override
    public VirusScanResult scanFile(byte[] fileBytes) throws IOException {
        if (EICAR == fileBytes) {
            return new VirusScanResult(VirusScanStatus.VIRUS_DETECTED, "EICAR");
        }
        return new VirusScanResult(VirusScanStatus.VIRUS_FREE, "Dummy Scan Provider");
    }
}
