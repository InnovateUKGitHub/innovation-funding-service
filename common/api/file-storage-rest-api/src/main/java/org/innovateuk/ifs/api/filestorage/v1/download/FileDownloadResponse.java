package org.innovateuk.ifs.api.filestorage.v1.download;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public final class FileDownloadResponse implements Serializable {

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

    /** The file payload */
    private byte[] payload;

    /** Any error message */
    private String error;

}
