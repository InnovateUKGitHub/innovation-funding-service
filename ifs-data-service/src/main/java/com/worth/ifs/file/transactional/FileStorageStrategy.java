package com.worth.ifs.file.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.domain.FileEntry;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.List;

/**
 * Represents a component that is able to advise where to store files based upon its strategy.
 */
public interface FileStorageStrategy {

    /**
     * Given a FileEntry, this method will return a list of path segments that represent an absolute path to the
     * containing folder, and a filename as the separate String
     *
     * @param file
     * @return
     */
    Pair<List<String>, String> getAbsoluteFilePathAndName(FileEntry file);

    /**
     * Given a FileEntry, see if this strategy can locate an existing file in its own storage
     *
     * @param file
     * @return
     */
    boolean exists(FileEntry file);

    /**
     * Creates a new File in the location that this strategy would place it
     *
     * @param fileEntry
     * @param temporaryFile
     * @return
     */
    ServiceResult<File> createFile(FileEntry fileEntry, File temporaryFile);

    ServiceResult<File> updateFile(FileEntry fileEntry, File temporaryFile);

    ServiceResult<File> moveFile(File temporaryFile);

    ServiceResult<Void> deleteFile(FileEntry fileEntry);
}
