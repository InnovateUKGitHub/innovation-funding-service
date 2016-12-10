package org.innovateuk.ifs.file.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.List;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.FILES_MOVE_DESTINATION_EXIST_SOURCE_DOES_NOT;
import static org.innovateuk.ifs.commons.service.BaseEitherBackedResult.filterErrors;
import static org.innovateuk.ifs.commons.service.ServiceResult.aggregate;
import static org.innovateuk.ifs.util.FileFunctions.pathElementsToFile;
import static java.util.stream.Collectors.toList;


/**
 * Functionality to move files
 */
public final class MoveFiles {

    public static ServiceResult<List<File>> moveAllFiles(final FileStorageStrategy from, final FileStorageStrategy to, final boolean ignoreAlreadyMovedErrors) {
        if (ignoreAlreadyMovedErrors) {
            return aggregate(filterErrors(moveAllFiles(from, to), f -> !f.is(FILES_MOVE_DESTINATION_EXIST_SOURCE_DOES_NOT)));
        } else {
            return aggregate(moveAllFiles(from, to));
        }
    }

    private static List<ServiceResult<File>> moveAllFiles(final FileStorageStrategy from, final FileStorageStrategy to) {
        final List<ServiceResult<File>> moveResults = from.allWithIds().stream().
                map(idAndPathOfFileToMove -> {
                    final Long id = idAndPathOfFileToMove.getKey();
                    final Pair<List<String>, String> path = idAndPathOfFileToMove.getValue();
                    final File fileToMove = new File(pathElementsToFile(path.getKey()), path.getValue());
                    return to.moveFile(id, fileToMove);
                }).
                collect(toList());
        return moveResults;
    }
}
