package com.worth.ifs.file.transactional;

import com.worth.ifs.file.domain.FileEntry;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * A strategy for storing files in a single folder with no hierarchy
 */
public class FlatFolderFileStorageStrategy extends BaseFileStorageStrategy {

    public FlatFolderFileStorageStrategy(String pathToStorageBase, String containingFolder) {
        super(pathToStorageBase, containingFolder);
    }

    @Override
    public Pair<List<String>, String> getAbsoluteFilePathAndName(FileEntry file) {
        return Pair.of(getAbsolutePathToFileUploadFolder(), file.getId() + "");
    }
}
