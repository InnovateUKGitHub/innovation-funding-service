package org.innovateuk.ifs.filestorage.virusscan.clam;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.filestorage.exception.ServiceException;
import org.innovateuk.ifs.filestorage.exception.VirusDetectedException;
import org.innovateuk.ifs.filestorage.virusscan.VirusScanProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.commands.scan.result.ScanResult;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ClamAvScanProvider implements VirusScanProvider {

    @Autowired
    private ClamavClient clamAVClient;

    @Scheduled(initialDelay = 2000L, fixedDelay = 10000L)
    public void getStats() {
        log.info(clamAVClient.version());
        log.info(clamAVClient.stats());
    }

    public void scanFile(byte[] fileBytes) {
        try {
            ScanResult scanResult = clamAVClient.scan(new ByteArrayInputStream(fileBytes));
            if (scanResult instanceof ScanResult.OK) {
                // OK
            } else if (scanResult instanceof ScanResult.VirusFound) {
                Map<String, Collection<String>> viruses = ((ScanResult.VirusFound) scanResult).getFoundViruses();
                throw new VirusDetectedException(viruses.keySet().stream().collect(Collectors.joining()));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(e);
        }

    }

}
