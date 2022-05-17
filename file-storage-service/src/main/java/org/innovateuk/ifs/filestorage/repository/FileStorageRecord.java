package org.innovateuk.ifs.filestorage.repository;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.innovateuk.ifs.api.filestorage.v1.upload.MimeCheckResult;
import org.innovateuk.ifs.api.filestorage.v1.upload.VirusScanStatus;

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

    @Column(nullable = false)
    private String storageProvider;

    @Column
    private VirusScanStatus virusScanStatus;

    @Column
    private String virusScanMessage;

    @Column
    private String storageLocation;

    @Column
    private MimeCheckResult mimeCheckResult;

}
