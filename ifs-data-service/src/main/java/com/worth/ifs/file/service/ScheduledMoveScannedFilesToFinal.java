package com.worth.ifs.file.service;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.transactional.FileStorageStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

import static com.worth.ifs.file.service.MoveFiles.moveAllFiles;


@Component
public class ScheduledMoveScannedFilesToFinal {

    private static final Log LOG = LogFactory.getLog(ScheduledMoveScannedFilesToFinal.class);

    @Autowired
    @Qualifier("finalFileStorageStrategy")
    private FileStorageStrategy finalFileStorageStrategy;

    @Autowired
    @Qualifier("scannedFileStorageStrategy")
    private FileStorageStrategy scannedFileStorageStrategy;


    @Scheduled(fixedDelayString = "${ifs.data.service.file.storage.virus.scanning.scanned.move.delay.millis}")
    public void moveFiles() {
        final ServiceResult<List<File>> listServiceResult = moveAllFiles(scannedFileStorageStrategy, finalFileStorageStrategy);
        if (listServiceResult.isFailure()){
            LOG.error("Failed to move some files from scanned to final: " + listServiceResult.getFailure());
        }
        else {
            LOG.debug("Copied files from scanned to final: " + listServiceResult.getSuccessObject());
        }
    }

}

