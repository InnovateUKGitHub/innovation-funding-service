package com.worth.ifs.file.domain;

import javax.persistence.*;

/**
 * Represents a File on the filesystem that can be referenced in the application.
 */
@Entity
public class FileEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String originalFilename;

    private String mimeType;

    private long filesizeBytes;

    public FileEntry() {
    }

    public FileEntry(Long id, String originalFilename, String mimeType, long filesizeBytes) {
        this.id = id;
        this.originalFilename = originalFilename;
        this.mimeType = mimeType;
        this.filesizeBytes = filesizeBytes;
    }

    public Long getId() {
        return id;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getMimeType() {
        return mimeType;
    }

    public long getFilesizeBytes() {
        return filesizeBytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileEntry fileEntry = (FileEntry) o;

        if (filesizeBytes != fileEntry.filesizeBytes) return false;
        if (id != null ? !id.equals(fileEntry.id) : fileEntry.id != null) return false;
        if (originalFilename != null ? !originalFilename.equals(fileEntry.originalFilename) : fileEntry.originalFilename != null)
            return false;
        if (mimeType != null ? !mimeType.equals(fileEntry.mimeType) : fileEntry.mimeType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (originalFilename != null ? originalFilename.hashCode() : 0);
        result = 31 * result + (mimeType != null ? mimeType.hashCode() : 0);
        result = 31 * result + (int) (filesizeBytes ^ (filesizeBytes >>> 32));
        return result;
    }
}
