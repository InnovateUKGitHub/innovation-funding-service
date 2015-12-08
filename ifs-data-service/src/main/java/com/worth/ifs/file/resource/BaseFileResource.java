package com.worth.ifs.file.resource;

/**
 * A Resource representation of a BaseFile.  Subclasses of this class will be the representations
 * of subclasses of BaseFile.
 */
public abstract class BaseFileResource {

    private Long id;

    protected BaseFileResource(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
