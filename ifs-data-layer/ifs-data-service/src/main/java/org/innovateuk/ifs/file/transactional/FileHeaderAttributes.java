package org.innovateuk.ifs.file.transactional;

import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.springframework.http.MediaType;

/**
 * A holder for some basic attributes about a file that is being validated and uploaded
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
