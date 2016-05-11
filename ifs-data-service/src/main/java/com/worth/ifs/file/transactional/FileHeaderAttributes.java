package com.worth.ifs.file.transactional;

import com.worth.ifs.file.resource.FileEntryResource;
import org.springframework.http.MediaType;

/**
 * TODO DW - document this class
 */
public class FileHeaderAttributes {

    private MediaType mediaType;
    private long contentLength;
    private String filename;

    public FileHeaderAttributes(MediaType mediaType, long contentLength, String filename) {
        this.mediaType = mediaType;
        this.contentLength = contentLength;
        this.filename = filename;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public long getContentLength() {
        return contentLength;
    }

    public String getFilename() {
        return filename;
    }

    public FileEntryResource toFileEntryResource() {
        return new FileEntryResource(null, filename, mediaType, contentLength);
    }
}
