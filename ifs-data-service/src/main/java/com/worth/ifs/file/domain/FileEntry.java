package com.worth.ifs.file.domain;

import org.springframework.http.MediaType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Represents a File on the filesystem that can be referenced in the application.
 */
@Entity
public class FileEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String mediaType;

    private long filesizeBytes;

    public FileEntry() {
    }

    public FileEntry(Long id, String originalFilename, MediaType mediaType, long filesizeBytes) {
        this(id, originalFilename, mediaType.toString(), filesizeBytes);
    }

    public FileEntry(Long id, String originalFilename, String mediaType, long filesizeBytes) {
        this.id = id;
        this.name = originalFilename;
        this.mediaType = mediaType;
        this.filesizeBytes = filesizeBytes;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMediaType() {
        return mediaType;
    }

    public long getFilesizeBytes() {
        return filesizeBytes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public void setFilesizeBytes(long filesizeBytes) {
        this.filesizeBytes = filesizeBytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileEntry fileEntry = (FileEntry) o;

        if (filesizeBytes != fileEntry.filesizeBytes) return false;
        if (id != null ? !id.equals(fileEntry.id) : fileEntry.id != null) return false;
        if (name != null ? !name.equals(fileEntry.name) : fileEntry.name != null) return false;
        if (mediaType != null ? !mediaType.equals(fileEntry.mediaType) : fileEntry.mediaType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (mediaType != null ? mediaType.hashCode() : 0);
        result = 31 * result + (int) (filesizeBytes ^ (filesizeBytes >>> 32));
        return result;
    }
}
