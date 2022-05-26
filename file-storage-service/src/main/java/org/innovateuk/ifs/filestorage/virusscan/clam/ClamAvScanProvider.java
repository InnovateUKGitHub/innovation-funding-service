package org.innovateuk.ifs.filestorage.virusscan.clam;

import com.diluv.clamchowder.ClamClient;
import com.diluv.clamchowder.ScanResult;
import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.filestorage.exception.ServiceException;
import org.innovateuk.ifs.filestorage.exception.VirusDetectedException;
import org.innovateuk.ifs.filestorage.virusscan.VirusScanProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Slf4j
public class ClamAvScanProvider implements VirusScanProvider {

    @Autowired
    private ClamClient clamAVClient;

    @Scheduled(initialDelay = 2000L, fixedDelay = 10000L)
    public void getStats() {
        try {
            log.info(clamAVClient.getVersion());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void scanFile(byte[] fileBytes) {
        ScanResult scanResult;
        try {
            scanResult = clamAVClient.scan(new ByteArrayInputStream(fileBytes));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(e);
        }
        switch (scanResult.getStatus()) {
            case OK:
                return;
            case FOUND:
                throw new VirusDetectedException(scanResult.getFound());
            case ERROR_TOO_BIG:
                throw new ServiceException("File too large to scan (see config) " + scanResult.getResponse());
            case UNKNOWN:
                throw new ServiceException(scanResult.getResponse());
        }
    }

}
