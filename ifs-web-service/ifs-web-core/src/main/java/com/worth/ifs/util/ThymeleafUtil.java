package com.worth.ifs.util;

import com.worth.ifs.config.IfSThymeleafDialect;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.servlet.http.HttpServletRequest;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.stripToNull;

/**
 * <p>
 * This class provides utility methods that can be used in replacement for lengthy OGNL or SpringEL expressions in Thymeleaf.
 * <p>
 * These methods are offered by the #ifsUtil utility object in Thymeleaf variable expressions.
 * </p>
 * <p>
 * #ifsUtil is added to the evaluation context by {@link IfSThymeleafDialect}.
 * </p>
 */
public class ThymeleafUtil {

    /**
     * Combines the request URI with the query string where present.
     *
     * @param request
     * @return
     */
    public String uriWithQueryString(final HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Cannot determine request URI with query string for null request.");
        }
        return request.getQueryString() == null ? "~" + request.getRequestURI() : format("~%s?%s", request.getRequestURI(), request.getQueryString());
    }

    /**
     * Given a maximum word count allowed for an item of content, count the number of words remaining until this limit is reached, for the specified content String.
     * @param maxWordCount
     * @param content
     * @return
     */
    public int wordsRemaining(Integer maxWordCount, String content) {
        return ofNullable(maxWordCount).map(maxWordCountValue -> maxWordCountValue - countWords(content)).orElse(0);
    }

    /**
     * Count the number of words in the specified content String.
     *
     * @param content
     * @return
     */
    private int countWords(String content) {
        return ofNullable(stripToNull(content)).map(contentValue -> {
            // clean any HTML markup from the value
            Document doc = Jsoup.parse(contentValue);
            String cleaned = doc.text();
            return cleaned.split("\\s+").length;
        }).orElse(0);
    }
}
