package com.worth.ifs.file.resource;

import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.util.JsonStatusResponse;

/**
 *
 */
public class FileEntryJsonStatusResponse extends JsonStatusResponse {

    private long fileEntryId;

    @SuppressWarnings("unused")
    private FileEntryJsonStatusResponse() {
        // for json marshalling
    }

    private FileEntryJsonStatusResponse(String message, FileEntry fileEntry) {
        super(message);
        this.fileEntryId = fileEntry.getId();
    }

    public static FileEntryJsonStatusResponse fileEntryCreated(FileEntry fileEntry) {
        return new FileEntryJsonStatusResponse("File created successfully", fileEntry);
    }

    public long getFileEntryId() {
        return fileEntryId;
    }
}
