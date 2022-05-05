package org.innovateuk.ifs.api.filestorage.util;

import com.google.common.hash.Hashing;

import java.util.Base64;

public class FileHashing {

    private FileHashing() {
        // private
    }

    /**
     * Consistent hash for files, md5 is fine for this purpose and s3 expects md5 base64 encoded which seems reasonable
     * @param payload bytes
     * @return the md5 hash
     */
    public static final String fileHash64(byte[] payload) {
        return Base64.getEncoder().encodeToString(Hashing.md5().hashBytes(payload).asBytes());
    }

}
