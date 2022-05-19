package org.innovateuk.ifs.api.filestorage.v1.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class FileUploadResponse implements Serializable {

    /** uuid reference for the file */
    private String fileId;

    /** The file type */
    private String mimeType;

    /** The file size in bytes */
    private long fileSizeBytes;

    /** The file name */
    private String fileName;

    /** The file checksum */
    private String md5Checksum;

}
