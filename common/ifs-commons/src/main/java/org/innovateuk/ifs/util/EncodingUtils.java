package org.innovateuk.ifs.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class EncodingUtils {

    private static final Logger LOG = LoggerFactory.getLogger(EncodingUtils.class);

    private EncodingUtils() {}

    public static String urlEncode(final String string){
        String encodedSearchString = string;
        try {
            encodedSearchString = URLEncoder.encode(encodedSearchString, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            LOG.info("Unsupported Encoding.  Skipping encoding and using original search string.", e);
        }
        return encodedSearchString;
    }

    public static String urlDecode(final String string){
        String encodedSearchString = string;
        try {
            encodedSearchString = URLDecoder.decode(encodedSearchString, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            LOG.info("Unsupported Decoding. Skipping decoding and using original search string.", e);
        }
        return encodedSearchString;
    }
}
