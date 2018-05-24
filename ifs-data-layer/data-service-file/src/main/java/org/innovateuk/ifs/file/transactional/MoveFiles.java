package org.innovateuk.ifs.file.transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    private static final Log LOG = LogFactory.getLog(MoveFiles.class);

    private MoveFiles() {}

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
                    ServiceResult<File> moveResult= to.moveFile(id, fileToMove);
                    moveResult.ifSuccessful(movedFile ->
                        LOG.info("[FileLogging] Moved file " + fileToMove.getAbsolutePath() + " to " + movedFile.getAbsolutePath())
                    );
                    return moveResult;
                }).
                collect(toList());
        return moveResults;
    }
}
