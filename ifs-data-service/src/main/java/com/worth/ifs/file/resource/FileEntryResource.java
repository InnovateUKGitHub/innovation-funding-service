package com.worth.ifs.file.resource;

import org.springframework.http.MediaType;

/**
 * A Resource representation of a FileEntry.  Subclasses of this class will be the representations
 * of subclasses of FileEntry.
 */
public class FileEntryResource {

    private Long id;
    private String name;
    private MediaType mediaType;
    private long filesizeBytes;

    public FileEntryResource() {
    }

    public FileEntryResource(Long id, String name, String mediaType, long filesizeBytes) {
        this(id, name, MediaType.parseMediaType(mediaType), filesizeBytes);
    }

    public FileEntryResource(Long id, String name, MediaType mediaType, long filesizeBytes) {
        this.id = id;
        this.name = name;
        this.mediaType = mediaType;
        this.filesizeBytes = filesizeBytes;
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

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
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
        if (mediaType != null ? !mediaType.equals(that.mediaType) : that.mediaType != null) return false;

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
