package org.innovateuk.ifs.filestorage.virusscan.clam;

import fi.solita.clamav.ClamAVClient;
import org.innovateuk.ifs.filestorage.exception.ServiceException;
import org.innovateuk.ifs.filestorage.exception.VirusDetectedException;
import org.innovateuk.ifs.filestorage.virusscan.VirusScanProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ClamAvScanProvider implements VirusScanProvider {

    @Autowired
    private ClamAVClient clamAVClient;

    public void scanFile(byte[] fileBytes) {
        try {
            byte[] scanResult = clamAVClient.scan(fileBytes);
            if (!ClamAVClient.isCleanReply(scanResult)) {
                throw new VirusDetectedException(new String(scanResult, StandardCharsets.US_ASCII));
            }
        } catch (IOException e) {
            throw new ServiceException(e);
        }

    }

}
