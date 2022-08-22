package org.innovateuk.ifs.api.filestorage.v1.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class FileUploadRequest implements Serializable {

    @NotNull
    /** uuid reference for the file */
    private String fileId;

    /** Originating system or subsystem for the storage request */
    private String systemId;

    /** The user requesting storage */
    private String userId;

    @NotNull
    /** The file payload */
    private byte[] payload;

    @NotNull
    /** The file type */
    private String mimeType;

    /** The file size in bytes */
    private long fileSizeBytes;

    /** The file name */
    private String fileName;

    /** The file checksum */
    private String md5Checksum;

}
