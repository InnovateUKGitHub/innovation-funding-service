package org.innovateuk.ifs.filestorage.repository;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Accessors(fluent = true)
public class FileStorageRecord {

    /** uuid reference for the file */
    @Id
    private String fileUuid;

    /** Originating system or subsystem for the storage request */
    @Column(nullable = false)
    private String systemId;

    /** The user requesting storage */
    @Column(nullable = false)
    private String userId;

    /** The file type */
    @Column(nullable = false)
    private String mimeType;

    @Column(nullable = false)
    private long fileSizeBytes;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String md5Checksum;

    @Column
    private String storageLocation;

    @Column
    private String error;

}
