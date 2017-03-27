package org.innovateuk.ifs.util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

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
        return ofNullable(stripToNull(stripHtml(content))).map(contentValue ->
                contentValue.split("\\s+").length).orElse(0);
    }

    /**
     * Strips any HTML markup by parsing the content into a HTML Document and extracting the text content.
     *
     * @param content
     * @return the given content without any HTML tags or attributes.
     */
    public static String stripHtml(String content) {
        return ofNullable(content).map(contentValue -> {
                    String cleaned = Jsoup.clean(content, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
                    return cleaned.trim().replaceAll("[ \t]+", " ");
                }
        ).orElse(null);
    }

    /**
     * Escapes characters in the content with HTML entities. Adds HTML line break elements.
     *
     * @param content
     * @return the given content with characters escaped by HTML entities and line breaks replaced with HTML line break elements.
     */
    public static String plainTextToHtml(String content) {
        String escaped = StringEscapeUtils.escapeHtml4(content);
        if (escaped != null) {
            return escaped.replaceAll("\\R", "<br/>");
        }
        return escaped;
    }
}
