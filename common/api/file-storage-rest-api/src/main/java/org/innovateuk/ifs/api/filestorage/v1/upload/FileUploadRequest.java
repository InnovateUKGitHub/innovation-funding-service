package org.innovateuk.ifs.api.filestorage.v1.upload;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
public final class FileUploadRequest implements Serializable {


    @NotNull
    /** uuid reference for the file */
    private final String fileId;

    /** Originating system or subsystem for the storage request */
    private final String systemId;

    /** The user requesting storage */
    private final String userId;

    @NotNull
    /** The file payload */
    private final byte[] payload;

    @NotNull
    /** The file type */
    private final String mimeType;

    /** The file size in bytes */
    private final long fileSizeBytes;

    /** The file name */
    private final String fileName;

    /** The file checksum */
    private final String md5Checksum;

}
