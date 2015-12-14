package com.worth.ifs.file.resource;

import org.springframework.util.MimeType;

/**
 * A Resource representation of a FileEntry.  Subclasses of this class will be the representations
 * of subclasses of FileEntry.
 */
public class FileEntryResource {

    private Long id;
    private String name;
    private String mimeType;
    private long filesizeBytes;

    public FileEntryResource() {
    }

    public FileEntryResource(Long id, String name, String mimeType, long filesizeBytes) {
        this.id = id;
        this.name = name;
        this.mimeType = mimeType;
        this.filesizeBytes = filesizeBytes;
    }

    public FileEntryResource(Long id, String name, MimeType mimeType, long filesizeBytes) {
        this(id, name, mimeType.getType(), filesizeBytes);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getFilesizeBytes() {
        return filesizeBytes;
    }

    public void setFilesizeBytes(long filesizeBytes) {
        this.filesizeBytes = filesizeBytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileEntryResource that = (FileEntryResource) o;

        if (filesizeBytes != that.filesizeBytes) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (mimeType != null ? !mimeType.equals(that.mimeType) : that.mimeType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (mimeType != null ? mimeType.hashCode() : 0);
        result = 31 * result + (int) (filesizeBytes ^ (filesizeBytes >>> 32));
        return result;
    }
}
