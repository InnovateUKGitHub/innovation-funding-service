package org.innovateuk.ifs.filestorage.storage.tika;

import org.apache.tika.Tika;
import org.innovateuk.ifs.api.filestorage.v1.upload.MimeCheckResult;
import org.springframework.beans.factory.annotation.Autowired;

public class TikaFileValidator {

    @Autowired
    private Tika tika;

    public MimeCheckResult validatePayload(String mimeType, byte[] payload, String fileName) {
        String detectedMimeType = tika.detect(payload, fileName);
        // Follows previous logic ported from data-service
        if (detectedMimeType == null) {
            return MimeCheckResult.MEDIA_TYPE_UNDETERMINED;
        } else if (detectedMimeType.equalsIgnoreCase(mimeType)) {
            return MimeCheckResult.MEDIA_TYPE_MATCH;
        }
        return MimeCheckResult.MEDIA_TYPE_MISMATCH;
    }
}
