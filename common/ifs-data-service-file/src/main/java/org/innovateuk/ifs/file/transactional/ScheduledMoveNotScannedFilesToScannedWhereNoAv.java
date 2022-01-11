package org.innovateuk.ifs.file.transactional;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

import static org.innovateuk.ifs.file.transactional.MoveFiles.moveAllFiles;


/**
 * Move files from the not scanned folder to the scanned folder on a periodic basis if AV scanning is not enabled.
 */
@Slf4j
@Component
public class ScheduledMoveNotScannedFilesToScannedWhereNoAv {

    @Autowired
    @Qualifier("temporaryHoldingFileStorageStrategy")
    private FileStorageStrategy temporaryHoldingFileStorageStrategy;

    @Autowired
    @Qualifier("scannedFileStorageStrategy")
    private FileStorageStrategy scannedFileStorageStrategy;

    @Value("${ifs.data.service.file.storage.virus.scanning.enabled}")
    private boolean virusScanningEnabled = true; // Default

    @Scheduled(fixedDelayString = "${ifs.data.service.file.storage.virus.scanning.scanned.move.delay.millis}")
    public void moveFiles() {
        if (!virusScanningEnabled) {
            final ServiceResult<List<File>> listServiceResult = moveAllFiles(temporaryHoldingFileStorageStrategy, scannedFileStorageStrategy, true);
            if (listServiceResult.isFailure()) {
                log.error("Failed to move some files from scanned to final: " + listServiceResult.getFailure());
            } else if (listServiceResult.getSuccess() != null && !listServiceResult.getSuccess().isEmpty()){
                log.debug("Copied files from scanned to final: " + listServiceResult.getSuccess());
            }
        }
    }

}

