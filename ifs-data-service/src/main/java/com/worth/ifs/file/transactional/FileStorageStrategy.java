package com.worth.ifs.file.transactional;

import com.worth.ifs.file.domain.FileEntry;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Represents a component that is able to advise where to store files based upon its strategy.
 */
public interface FileStorageStrategy {

    /**
     * Given a FileENtry, this method will return a list of path segments that represent an absolute path to the
     * containing folder, and a filename as the separate String
     *
     * @param file
     * @return
     */
    Pair<List<String>, String> getAbsoluteFilePathAndName(FileEntry file);
}
