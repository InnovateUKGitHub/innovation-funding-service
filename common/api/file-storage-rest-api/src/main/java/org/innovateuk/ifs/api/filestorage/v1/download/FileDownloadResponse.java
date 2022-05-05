package org.innovateuk.ifs.api.filestorage.v1.download;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Builder
@Accessors(fluent = true)
public final class FileDownloadResponse {

    /** uuid reference for the file */
    private final String fileId;

    /** Virus scan status */
    private final String virusScanStatus;

    /** Result of the virus scan from provided scanner */
    private final String virusScanResultMessage;

    /** The file type */
    private final String mimeType;

    /** The file size in bytes */
    private final long fileSizeBytes;

    /** The file name */
    private final String fileName;

    /** The file checksum */
    private final String md5Checksum;

    /** The file payload */
    private final byte[] payload;
}
