package com.worth.ifs.file.resource;

/**
 * A Resource representation of a FileEntry.  Subclasses of this class will be the representations
 * of subclasses of FileEntry.
 */
public class FileEntryResource {

    private Long id;

    public FileEntryResource() {
    }

    public FileEntryResource(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
