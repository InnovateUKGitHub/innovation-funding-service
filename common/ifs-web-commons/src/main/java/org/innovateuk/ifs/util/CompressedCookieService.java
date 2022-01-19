package org.innovateuk.ifs.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Service to compress and decompress cookie values.
 */
@Service
@Slf4j
public class CompressedCookieService extends AbstractCookieService {

    @Override
    protected String getValueToSave(String value) {
        try {
            return CompressionUtil.getCompressedString(value);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Failed to compress cookie", e);
            // copied existing exception flow
            // Ticket created to review error squashing
            // https://devops.innovateuk.org/issue-tracking/browse/IFS-10941
            return "";
        }
    }

    @Override
    protected String getValueFromCookie(String value) {
        try {
            return CompressionUtil.getDecompressedString(value);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Failed to compress cookie", e);
            // copied existing exception flow
            // Ticket created to review error squashing
            // https://devops.innovateuk.org/issue-tracking/browse/IFS-10941
            return "";
        }
    }
}
