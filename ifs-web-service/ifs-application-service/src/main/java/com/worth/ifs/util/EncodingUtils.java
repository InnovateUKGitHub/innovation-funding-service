package com.worth.ifs.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class EncodingUtils {
    private static final Log LOG = LogFactory.getLog(EncodingUtils.class);

    public static String urlEncode(final String string){
        String encodedSearchString = string;
        try {
            encodedSearchString = URLEncoder.encode(encodedSearchString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.info("Unsupported Encoding.  Skipping encoding and using original search string.");
        }
        return encodedSearchString;
    }
}
