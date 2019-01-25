package org.innovateuk.ifs.util;

import org.innovateuk.ifs.user.resource.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static java.lang.String.format;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.springframework.util.StringUtils.hasText;

/**
 * A utility class to generate commonly used absolute URLs / redirect patterns
 */
@Component
public class NavigationUtils {

    private static final Map<Role, String> DEFAULT_LANDING_PAGE_URLS_FOR_ROLES =
            asMap(
                    ASSESSOR, "assessment/assessor/dashboard",
                    APPLICANT, "applicant/dashboard",
                    COMP_ADMIN, "management/dashboard",
                    PROJECT_FINANCE, "management/dashboard",
                    INNOVATION_LEAD, "management/dashboard",
                    IFS_ADMINISTRATOR, "management/dashboard",
                    SUPPORT, "management/dashboard",
                    MONITORING_OFFICER, "applicant/dashboard",
                    STAKEHOLDER, "management/dashboard"
            );

    @Value("${ifs.live.projects.landing.page.url}")
    private String liveProjectsLandingPageUrl;

    NavigationUtils() {}

    public String getRedirectToDashboardUrlForRole(Role role) {

        if (LIVE_PROJECTS_USER.equals(role)) {
            return "redirect:" + liveProjectsLandingPageUrl;
        }

        String roleUrl = DEFAULT_LANDING_PAGE_URLS_FOR_ROLES.get(role);

        return format("redirect:/%s", hasText(roleUrl) ? roleUrl : "dashboard");
    }

    public String getDirectDashboardUrlForRole(HttpServletRequest request, Role role) {

        if (LIVE_PROJECTS_USER.equals(role)) {
            return liveProjectsLandingPageUrl;
        }

        String roleUrl = DEFAULT_LANDING_PAGE_URLS_FOR_ROLES.get(role);

        return getDirectToSameDomainUrl(request, roleUrl);
    }

    public String getRedirectToLandingPageUrl(HttpServletRequest request) {
        return String.format("redirect:%s://%s:%s",
                request.getScheme(),
                request.getServerName(),
                request.getServerPort());
    }

    public String getDirectLandingPageUrl(HttpServletRequest request) {
        return String.format("%s://%s:%s",
                request.getScheme(),
                request.getServerName(),
                request.getServerPort());
    }

    public String getRedirectToSameDomainUrl(HttpServletRequest request, String url) {
        return String.format("redirect:%s://%s:%s/%s",
                request.getScheme(),
                request.getServerName(),
                request.getServerPort(),
                url);
    }

    public String getDirectToSameDomainUrl(HttpServletRequest request, String url) {
        return String.format("%s://%s:%s/%s",
                request.getScheme(),
                request.getServerName(),
                request.getServerPort(),
                url);
    }
}
