package org.innovateuk.ifs.analytics;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;

public class GoogleAnalyticsUtil {
    public static final String EMPTY_VALUE = "empty";

    private GoogleAnalyticsUtil() {}

    public static void addGoogleAnalytics(ModelAndView modelAndView, String googleAnalyticsKeys) {
        if (StringUtils.hasText(googleAnalyticsKeys) && !googleAnalyticsKeys.equals(EMPTY_VALUE)) {
            modelAndView.getModel().put("GoogleAnalyticsTrackingID", googleAnalyticsKeys);
        }
    }
}
