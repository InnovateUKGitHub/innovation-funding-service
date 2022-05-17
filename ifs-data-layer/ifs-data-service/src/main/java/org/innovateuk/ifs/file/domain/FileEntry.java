package org.innovateuk.ifs.file.domain;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String mediaType;

    private long filesizeBytes;

    private String fileUuid;

    private String md5;

    public FileEntry() {
    	// no-arg constructor
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

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileUuid() { return fileUuid; }

    public void setFileUuid(String fileUuid) { this.fileUuid = fileUuid; }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
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
        if (fileUuid != null ? !fileUuid.equals(fileEntry.fileUuid) : fileEntry.fileUuid != null) return false;
        return md5 != null ? md5.equals(fileEntry.md5) : fileEntry.md5 == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (mediaType != null ? mediaType.hashCode() : 0);
        result = 31 * result + (int) (filesizeBytes ^ (filesizeBytes >>> 32));
        result = 31 * result + (fileUuid != null ? fileUuid.hashCode() : 0);
        result = 31 * result + (md5 != null ? md5.hashCode() : 0);
        return result;
    }
}
