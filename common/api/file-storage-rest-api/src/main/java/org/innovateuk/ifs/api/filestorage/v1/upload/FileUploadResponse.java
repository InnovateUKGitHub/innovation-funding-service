package org.innovateuk.ifs.api.filestorage.v1.upload;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Optional;

@Data
@Builder
public final class FileUploadResponse implements Serializable {

    /** uuid reference for the file */
    private final String fileId;

    /** The file type */
    private final String mimeType;

    /** The file size in bytes */
    private final long fileSizeBytes;

    /** The file name */
    private final String fileName;

    /** The file checksum */
    private final String md5Checksum;

}
