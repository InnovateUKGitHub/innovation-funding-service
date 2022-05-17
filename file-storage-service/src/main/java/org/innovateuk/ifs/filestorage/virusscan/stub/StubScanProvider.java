package org.innovateuk.ifs.filestorage.virusscan.stub;

import org.innovateuk.ifs.api.filestorage.v1.upload.VirusScanStatus;
import org.innovateuk.ifs.filestorage.exception.VirusDetectedException;
import org.innovateuk.ifs.filestorage.virusscan.VirusScanProvider;
import org.innovateuk.ifs.filestorage.virusscan.VirusScanResult;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class StubScanProvider implements VirusScanProvider {

    private static final byte[] EICAR
            = "X5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*".getBytes(StandardCharsets.UTF_8);

    @Override
    public void scanFile(byte[] fileBytes) throws VirusDetectedException {
        if (Arrays.equals(EICAR, fileBytes)) {
            throw new VirusDetectedException("EICAR TEST FILE");
        }
    }
}
