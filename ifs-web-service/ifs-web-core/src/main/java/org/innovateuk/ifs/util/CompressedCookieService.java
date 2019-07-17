package org.innovateuk.ifs.util;

import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.util.CompressionUtil.getCompressedString;
import static org.innovateuk.ifs.util.CompressionUtil.getDecompressedString;

/**
 * Service to compress and decompress cookie values.
 */
@Service
public class CompressedCookieService extends AbstractCookieService {

    @Override
    protected String getValueToSave(String value) {
        return getCompressedString(value);
    }

    @Override
    protected String getValueFromCookie(String value) {
        return getDecompressedString(value);
    }
}
