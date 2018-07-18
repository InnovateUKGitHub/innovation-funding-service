package org.innovateuk.ifs.util;

import javax.servlet.http.HttpServletRequest;

public class RedirectUtils {
    private RedirectUtils() {}

    public static String buildRedirect(HttpServletRequest request, String url) {
        return String.format("%s://%s:%s/%s",
                request.getScheme(),
                request.getServerName(),
                request.getServerPort(),
                url);
    }
}
