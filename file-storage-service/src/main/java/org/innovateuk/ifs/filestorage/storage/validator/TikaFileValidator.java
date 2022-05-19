package org.innovateuk.ifs.filestorage.storage.validator;

import org.apache.tika.Tika;
import org.innovateuk.ifs.filestorage.exception.MimeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;

public class TikaFileValidator {

    @Autowired
    private Tika tika;

    public void validatePayload(String mimeType, byte[] payload, String fileName) {
        String detectedMimeType = tika.detect(payload, fileName);
        // Follows previous logic ported from data-service
        if (detectedMimeType != null && !detectedMimeType.equalsIgnoreCase(mimeType)) {
            throw new MimeMismatchException(detectedMimeType + " when " + mimeType + " was specified");
        }
    }
}
