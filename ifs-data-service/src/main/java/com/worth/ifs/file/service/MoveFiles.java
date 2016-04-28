package com.worth.ifs.file.service;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.transactional.FileStorageStrategy;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.util.List;

import static com.worth.ifs.util.FileFunctions.pathElementsToFile;


public final class MoveFiles {


    @Autowired
    @Qualifier("finalFileStorageStrategy")
    private FileStorageStrategy finalFileStorageStrategy;

    @Autowired
    @Qualifier("scannedFileStorageStrategy")
    private FileStorageStrategy scannedFileStorageStrategy;


    @Scheduled(fixedDelayString = "${ifs.data.service.file.storage.virus.scanning.scanned.move.delay.millis}")
    public void moveFiles() {

    }

    public static void moveAllFiles(final FileStorageStrategy from, final FileStorageStrategy to) {
        for (Pair<List<String>, String> temp : from.getAll()) {
            final ServiceResult<File> fileServiceResult = from.fileEntryIdFromPath(temp).andOnSuccess(id -> {
                final File fileToMove = new File(pathElementsToFile(temp.getKey()), temp.getValue());
                return to.moveFile(id, fileToMove);
            });
            if (fileServiceResult.isFailure()) {

            }
        }
    }
}

