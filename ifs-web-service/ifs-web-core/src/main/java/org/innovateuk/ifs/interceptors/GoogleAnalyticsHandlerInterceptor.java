package org.innovateuk.ifs.interceptors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.innovateuk.ifs.analytics.GoogleAnalyticsUtil.EMPTY_VALUE;
import static org.innovateuk.ifs.analytics.GoogleAnalyticsUtil.addGoogleAnalytics;

public class GoogleAnalyticsHandlerInterceptor extends HandlerInterceptorAdapter {

    @Value("${ifs.web.googleanalytics.trackingid:" + EMPTY_VALUE + "}")
    private String googleAnalyticsKeys;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (modelAndView != null && !(modelAndView.getView() instanceof RedirectView || modelAndView.getViewName().startsWith("redirect:"))) {
            addGoogleAnalytics(modelAndView, googleAnalyticsKeys);
        }
    }

}
