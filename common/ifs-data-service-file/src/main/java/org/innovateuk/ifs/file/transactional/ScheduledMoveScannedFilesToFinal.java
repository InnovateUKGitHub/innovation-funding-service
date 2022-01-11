package org.innovateuk.ifs.file.transactional;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

import static org.innovateuk.ifs.file.transactional.MoveFiles.moveAllFiles;


/**
 * Move files from the scanned folder to the final directory structure on a periodic basis.
 */
@Slf4j
@Component
public class ScheduledMoveScannedFilesToFinal {

    @Autowired
    @Qualifier("finalFileStorageStrategy")
    private FileStorageStrategy finalFileStorageStrategy;

    @Autowired
    @Qualifier("scannedFileStorageStrategy")
    private FileStorageStrategy scannedFileStorageStrategy;


    @Scheduled(fixedDelayString = "${ifs.data.service.file.storage.virus.scanning.scanned.move.delay.millis}")
    public void moveFiles() {
        final ServiceResult<List<File>> listServiceResult = moveAllFiles(scannedFileStorageStrategy, finalFileStorageStrategy, true);
        if (listServiceResult.isFailure()) {
            log.error("Failed to move some files from scanned to final: " + listServiceResult.getFailure());
        } else if (listServiceResult.getSuccess() != null && !listServiceResult.getSuccess().isEmpty()) {
            log.debug("Copied files from scanned to final: " + listServiceResult.getSuccess());
        }
    }

}

