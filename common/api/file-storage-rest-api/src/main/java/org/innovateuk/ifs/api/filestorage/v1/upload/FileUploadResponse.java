package org.innovateuk.ifs.api.filestorage.v1.upload;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.util.MimeType;

import java.util.UUID;

@Data
@Builder
@Accessors(fluent = true)
public final class FileUploadResponse {

    /** uuid reference for the file */
    private final UUID fileId;

    /** Virus scan status */
    private final VirusScanStatus virusScanStatus;

    /** Result of the virus scan from provided scanner */
    private final String virusScanResult;

    /** The file type */
    private final MimeType mimeType;

    /** The file size in bytes */
    private final long fileSizeBytes;

    /** The file name */
    private final String fileName;

    /** The file checksum */
    private final String checksum;

}
