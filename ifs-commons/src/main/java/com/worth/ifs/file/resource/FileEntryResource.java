package com.worth.ifs.file.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.http.MediaType;

/**
 * A Resource representation of a FileEntry.  Subclasses of this class will be the representations
 * of subclasses of FileEntry.
 */
public class FileEntryResource {

    private Long id;
    private String name;
    private String mediaType;
    private long filesizeBytes;

    public FileEntryResource() {
        // for JSON marshalling
    }

    public FileEntryResource(Long id, String name, String mediaType, long filesizeBytes) {
        this.id = id;
        this.name = name;
        this.mediaType = mediaType;
        this.filesizeBytes = filesizeBytes;
    }

    public FileEntryResource(Long id, String name, MediaType mediaType, long filesizeBytes) {
        this(id, name, mediaType.toString(), filesizeBytes);
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

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
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

        return new EqualsBuilder()
                .append(filesizeBytes, that.filesizeBytes)
                .append(id, that.id)
                .append(name, that.name)
                .append(mediaType, that.mediaType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(mediaType)
                .append(filesizeBytes)
                .toHashCode();
    }
}
