package com.worth.ifs.file.resource;

import com.worth.ifs.file.domain.FileEntry;

/**
 *
 */
public class FileEntryResourceAssembler {

    public static FileEntry valueOf(FileEntryResource resource) {
        return new FileEntry(resource.getId(), null, null, 0L);
    }
}
