package org.innovateuk.ifs.api.filestorage.util;

import com.google.common.hash.Hashing;

public class FileHashing {

    private FileHashing() {
        // private
    }

    /**
     * Consistent hash for files, md5 is fine for this purpose and s3 expects md5
     * @param payload bytes
     * @return the md5 hash
     */
    public static final String fileHash(byte[] payload) {
        return Hashing.md5().hashBytes(payload).toString();
    }

}
