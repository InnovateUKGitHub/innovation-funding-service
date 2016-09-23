package com.worth.ifs.util;

import com.worth.ifs.config.IfSThymeleafDialect;

import javax.servlet.http.HttpServletRequest;

import static com.worth.ifs.util.StringFunctions.countWords;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;

/**
 * This class provides utility methods that can be used in replacement for lengthy OGNL or SpringEL expressions in Thymeleaf.
 * <p>
 * These methods are offered by the #ifsUtil utility object in Thymeleaf variable expressions.
 * <p>
 * #ifsUtil is added to the evaluation context by {@link IfSThymeleafDialect}.
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
     * <p>
     * Given a maximum word count allowed for an item of content, count the number of words remaining until this limit is reached, for the specified content {@link String}.
     * If the content contains HTML markup then this will first be parsed into a HTML Document and the text content will be extracted.
     * </p>
     *
     * @param maxWordCount
     * @param content
     * @return
     */
    public int wordsRemaining(Integer maxWordCount, String content) {
        return ofNullable(maxWordCount).map(maxWordCountValue -> maxWordCountValue - countWords(content)).orElse(0);
    }
}
