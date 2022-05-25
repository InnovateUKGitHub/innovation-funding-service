package org.innovateuk.ifs.filestorage.repository;

import com.google.common.base.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileStorageRecord that = (FileStorageRecord) o;
        return fileSizeBytes == that.fileSizeBytes && Objects.equal(systemId, that.systemId) && Objects.equal(userId, that.userId) && Objects.equal(mimeType, that.mimeType) && Objects.equal(fileName, that.fileName) && Objects.equal(md5Checksum, that.md5Checksum) && Objects.equal(storageLocation, that.storageLocation) && Objects.equal(error, that.error);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(systemId, userId, mimeType, fileSizeBytes, fileName, md5Checksum, storageLocation, error);
    }
}
