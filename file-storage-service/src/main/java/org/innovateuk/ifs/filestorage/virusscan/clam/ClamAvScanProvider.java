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
import java.nio.charset.StandardCharsets;

@Slf4j
public class ClamAvScanProvider implements VirusScanProvider {

    private static final byte[] CMD_STATS = encode("STATS", true);

    @Autowired
    private ClamClient clamAVClient;

    @Scheduled(initialDelay = 2000L, fixedDelay = 30000L)
    public void getStats() {
        try {
            log.debug(clamAVClient.getVersion());
            log.debug(new String(clamAVClient.sendCommand(CMD_STATS), StandardCharsets.US_ASCII));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void scanFile(byte[] fileBytes) {
        ScanResult scanResult = clientRead(fileBytes);
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

    private ScanResult clientRead(byte[] fileBytes) {
        try {
            return clamAVClient.scan(new ByteArrayInputStream(fileBytes));
        } catch (Exception e) {
            log.error("First Failure: " + e.getMessage(), e);
            try {
                return clamAVClient.scan(new ByteArrayInputStream(fileBytes));
            } catch (Exception retryfailure) {
                log.error("Retry Failure: " + e.getMessage(), e);
            }
            throw new ServiceException(e);
        }
    }

    private static byte[] encode (String command, boolean outgoing) {
        final String toEncode = outgoing ? "z" + command + "\0" : command + "\0";
        return toEncode.getBytes(StandardCharsets.US_ASCII);
    }

}