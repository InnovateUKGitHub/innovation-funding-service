package org.innovateuk.ifs.util;

import javax.servlet.http.HttpServletRequest;

public class RedirectUtils {
    private RedirectUtils() {}

    public static String redirectToApplicationService(HttpServletRequest request, String url) {
        return buildRedirect(request, url, "");
    }

    public static String redirectToSurveyService(HttpServletRequest request, String url) {
        return buildRedirect(request, url, "survey");
    }

    public static String redirectToAssementService(HttpServletRequest request, String url) {
        return buildRedirect(request, url, "assessment");
    }

    public static String redirectToCompetitionManagementService(HttpServletRequest request, String url) {
        return buildRedirect(request, url, "management");
    }

    public static String redirectToProjectSetupManagementService(HttpServletRequest request, String url) {
        return buildRedirect(request, url, "project-setup-management");
    }

    public static String redirectToProjectSetupService(HttpServletRequest request, String url) {
        return buildRedirect(request, url, "project-setup");
    }

    private static String buildRedirect(HttpServletRequest request, String url, String application) {
        return String.format("redirect:%s://%s:%s/%s/%s",
                request.getScheme(),
                request.getServerName(),
                request.getServerPort(),
                application,
                url);


    }
}
