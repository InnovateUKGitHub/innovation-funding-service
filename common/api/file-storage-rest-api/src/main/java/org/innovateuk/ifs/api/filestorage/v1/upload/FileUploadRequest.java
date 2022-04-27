package org.innovateuk.ifs.api.filestorage.v1.upload;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.MimeType;

import java.util.UUID;

@Data
@AllArgsConstructor
public final class FileUploadRequest {

    /** uuid reference for the file */
    private final UUID fileId;

    /** Originating system or subsystem for the storage request */
    private final String systemId;

    /** The user requesting storage */
    private final String userId;

    /** The file payload */
    private final byte[] payload;

    /** The file type */
    private final MimeType mimeType;

    /** The file size in bytes */
    private final long fileSizeBytes;

    /** The file name */
    private final String fileName;

    /** The file checksum */
    private final String checksum;

}
