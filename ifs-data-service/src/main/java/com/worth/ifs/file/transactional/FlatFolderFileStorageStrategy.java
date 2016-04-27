package com.worth.ifs.file.transactional;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.List;

import static com.worth.ifs.commons.error.CommonFailureKeys.FILES_UNABLE_TO_FIND_FILE_ENTRY_ID_FROM_FILE;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.FileFunctions.pathElementsToFile;
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
    public List<Pair<List<String>, String>> getAll() {
        final File uploadFolder = pathElementsToFile(getAbsolutePathToFileUploadFolder());
        return asList(uploadFolder.listFiles()).stream().
                filter(f -> isLong(f.getName())).
                filter(f -> f.isDirectory()).
                map(f -> Pair.of(getAbsolutePathToFileUploadFolder(), f.getName())).
                collect(toList());
    }

    @Override
    public ServiceResult<Long> fileEntryIdFromPath(Pair<List<String>, String> path) {
        if (getAbsolutePathToFileUploadFolder().equals(path.getLeft())){
            if (isLong(path.getRight())){
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
