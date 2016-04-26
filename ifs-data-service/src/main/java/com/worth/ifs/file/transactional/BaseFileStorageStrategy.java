package com.worth.ifs.file.transactional;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.worth.ifs.util.CollectionFunctions.combineLists;
import static com.worth.ifs.util.CollectionFunctions.simpleFilterNot;
import static java.io.File.separator;
import static java.util.Arrays.asList;

/**
 * Represents a component that, given a FileEntry to store, decides how best to store it and stores it on the filesystem.
 * This base class is for a storage mechanism that expects to have a pointer to a base folder that all storage is performed
 * within, as well as a containing folder within that storage base that this strategy will put all files into (and create
 * if it doesn't yet exist)
 */
abstract class BaseFileStorageStrategy implements FileStorageStrategy {

    protected String pathToStorageBase;
    protected String containingFolder;

    public BaseFileStorageStrategy(String pathToStorageBase, String containingFolder) {
        this.pathToStorageBase = pathToStorageBase;
        this.containingFolder = containingFolder;
    }

    protected List<String> getAbsolutePathToFileUploadFolder() {
        return getAbsolutePathToFileUploadFolder(separator);
    }

    List<String> getAbsolutePathToFileUploadFolder(String fileSeparator) {

        // get an absolute path to the file upload folder, split into segments
        String separatorToUseInSplit = separatorForSplit(fileSeparator);
        List<String> pathToStorageBaseAsSegments = asList(pathToStorageBase.split(separatorToUseInSplit));
        List<String> fullPathToContainingFolderWithEmptyElements = combineLists(pathToStorageBaseAsSegments, containingFolder);
        List<String> fullPathWithNoEmptyElements = simpleFilterNot(fullPathToContainingFolderWithEmptyElements, StringUtils::isBlank);

        // then if using a *nix path that starts with file separator e.g. /tmp/path, ensure that we retain the leading "/"
        if (pathToStorageBase.startsWith(fileSeparator)) {
            fullPathWithNoEmptyElements.set(0, fileSeparator + fullPathWithNoEmptyElements.get(0));
        }

        return fullPathWithNoEmptyElements;
    }

    private String separatorForSplit(String fileSeparator) {
        if ("\\".equals(fileSeparator)) {
            return "\\\\";
        }
        return fileSeparator;
    }
}