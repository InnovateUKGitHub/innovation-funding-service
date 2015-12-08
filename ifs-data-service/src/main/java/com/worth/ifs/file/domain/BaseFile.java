package com.worth.ifs.file.domain;

import javax.persistence.*;

/**
 * Represents the common elements of a File in the application.  Subclasses of this will
 * hold file-type specific information that is relevant to their context and not common amongst
 * all files.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class BaseFile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    public Long getId() {
        return id;
    }
}
