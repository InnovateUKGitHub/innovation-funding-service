package com.worth.ifs.util;

import com.worth.ifs.config.IfSThymeleafDialect;

import javax.servlet.http.HttpServletRequest;

import static java.lang.String.format;

/**
 * <p>
 * This class provides utility methods that can be used in replacement for lengthy OGNL or SpringEL expressions in Thymeleaf.
 *
 * These methods are offered by the #ifsUtil utility object in Thymeleaf variable expressions.
 * </p>
 * <p>
 * #ifsUtil is added to the evaluation context by {@link IfSThymeleafDialect}.
 * </p>
 */
public class ThymeleafUtil {

    /**
     * Combines the request URI with the query string where present.
     * @param request
     * @return
     */
    public String uriWithQueryString(final HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Cannot determine request URI with query string for null request.");
        }
        return request.getQueryString() == null ? "~" + request.getRequestURI() : format("~%s?%s", request.getRequestURI(), request.getQueryString());
    }
}
