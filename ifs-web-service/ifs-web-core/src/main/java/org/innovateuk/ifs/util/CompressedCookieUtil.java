package org.innovateuk.ifs.util;

import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.util.CompressionUtil.getCompressedString;
import static org.innovateuk.ifs.util.CompressionUtil.getDecompressedString;

@Service
public class CompressedCookieUtil extends AbstractCookieUtil {

    @Override
    protected String getValueToSave(String value) {
        return getCompressedString(value);
    }

    @Override
    protected String getValueFromCookie(String value) {
        return getDecompressedString(value);
    }
}
