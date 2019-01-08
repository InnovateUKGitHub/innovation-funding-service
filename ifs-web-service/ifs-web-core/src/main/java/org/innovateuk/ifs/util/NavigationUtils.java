package org.innovateuk.ifs.util;

import javax.servlet.http.HttpServletRequest;

/**
 * A utility class to generate commonly used absolute URLs / redirect patterns
 */
public class NavigationUtils {
    private NavigationUtils() {}

    public static String getRedirectToLandingPageUrl(HttpServletRequest request) {
        return String.format("redirect:%s://%s:%s",
                request.getScheme(),
                request.getServerName(),
                request.getServerPort());
    }

    public static String getDirectLandingPageUrl(HttpServletRequest request) {
        return String.format("%s://%s:%s",
                request.getScheme(),
                request.getServerName(),
                request.getServerPort());
    }

    public static String getRedirectToSameDomainUrl(HttpServletRequest request, String url) {
        return String.format("redirect:%s://%s:%s/%s",
                request.getScheme(),
                request.getServerName(),
                request.getServerPort(),
                url);
    }
}
