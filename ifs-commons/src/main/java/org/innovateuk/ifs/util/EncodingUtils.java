package org.innovateuk.ifs.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public final class EncodingUtils {
    private static final Log LOG = LogFactory.getLog(EncodingUtils.class);

    private EncodingUtils() {}

    public static String urlEncode(final String string){
        String encodedSearchString = string;
        try {
            encodedSearchString = URLEncoder.encode(encodedSearchString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.info("Unsupported Encoding.  Skipping encoding and using original search string.", e);
        }
        return encodedSearchString;
    }

    public static String urlDecode(final String string){
        String encodedSearchString = string;
        try {
            encodedSearchString = URLDecoder.decode(encodedSearchString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.info("Unsupported Decoding. Skipping decoding and using original search string.", e);
        }
        return encodedSearchString;
    }
}
