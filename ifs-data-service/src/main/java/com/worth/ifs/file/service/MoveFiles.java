package com.worth.ifs.file.service;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.transactional.FileStorageStrategy;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.List;

import static com.worth.ifs.commons.service.ServiceResult.aggregate;
import static com.worth.ifs.util.FileFunctions.pathElementsToFile;
import static java.util.stream.Collectors.toList;


public final class MoveFiles {

    public static ServiceResult<List<File>> moveAllFiles(final FileStorageStrategy from, final FileStorageStrategy to) {
        final List<ServiceResult<File>> moveResults = from.allWithIds().stream().
                map(idAndPathOfFileToMove -> {
                    final Long id = idAndPathOfFileToMove.getKey();
                    final Pair<List<String>, String> path = idAndPathOfFileToMove.getValue();
                    final File fileToMove = new File(pathElementsToFile(path.getKey()), path.getValue());
                    return to.moveFile(id, fileToMove);
                }).
                collect(toList());
        return aggregate(moveResults);
    }
}