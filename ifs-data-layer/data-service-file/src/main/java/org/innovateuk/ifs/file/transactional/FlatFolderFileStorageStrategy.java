package org.innovateuk.ifs.file.transactional;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.FILES_UNABLE_TO_FIND_FILE_ENTRY_ID_FROM_FILE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.FileFunctions.pathElementsToFile;
import static java.lang.Long.parseLong;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

/**
 * A strategy for storing files in a single folder with no hierarchy
 */
public class FlatFolderFileStorageStrategy extends BaseFileStorageStrategy {

    public FlatFolderFileStorageStrategy(String pathToStorageBase, String containingFolder) {
        super(pathToStorageBase, containingFolder);
    }

    @Override
    public Pair<List<String>, String> getAbsoluteFilePathAndName(Long fileEntryId) {
        return Pair.of(getAbsolutePathToFileUploadFolder(), fileEntryId + "");
    }

    @Override
    public List<Pair<List<String>, String>> all() {
        final File uploadFolder = pathElementsToFile(getAbsolutePathToFileUploadFolder());
        final File[] files = uploadFolder.listFiles();
        if (files != null) {
            return asList(files).stream().
                    filter(f -> isLong(f.getName())).
                    filter(f -> f.isFile()).
                    map(f -> Pair.of(getAbsolutePathToFileUploadFolder(), f.getName())).
                    collect(toList());
        }
        return new ArrayList<>();
    }




    @Override
    public ServiceResult<Long> fileEntryIdFromPath(Pair<List<String>, String> path) {
        if (getAbsolutePathToFileUploadFolder().equals(path.getLeft())) {
            if (isLong(path.getRight())) {
                return serviceSuccess(parseLong(path.getRight()));
            }
        }
        return serviceFailure(new Error(FILES_UNABLE_TO_FIND_FILE_ENTRY_ID_FROM_FILE));
    }

    private final static boolean isLong(final String toParse) {
        try {
            parseLong(toParse);
            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }

}
