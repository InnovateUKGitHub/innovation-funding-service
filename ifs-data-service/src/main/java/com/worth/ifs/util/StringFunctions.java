package com.worth.ifs.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.stripToNull;

/**
 * Utility methods for String objects.
 */
public class StringFunctions {

    /**
     * Count the number of words in a given content {@link String}. If the content contains HTML markup then this will first be parsed into a HTML Document and the text content will be extracted.
     *
     * @param content
     * @return the number of words in the given content or zero if the content is {@code null}.
     */
    public static int countWords(String content) {
        return ofNullable(stripToNull(content)).map(contentValue -> {
            // clean any HTML markup from the value
            Document doc = Jsoup.parse(contentValue);
            String cleaned = doc.text();
            return cleaned.split("\\s+").length;
        }).orElse(0);
    }
}