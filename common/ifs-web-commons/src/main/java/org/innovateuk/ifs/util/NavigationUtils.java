package org.innovateuk.ifs.util;

import org.innovateuk.ifs.user.resource.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
                    MONITORING_OFFICER, "project-setup/monitoring-officer/dashboard",
                    STAKEHOLDER, "management/dashboard",
                    EXTERNAL_FINANCE, "management/dashboard",
                    KNOWLEDGE_TRANSFER_ADVISER, "assessment/assessor/dashboard",
                    SUPPORTER, "assessment/supporter/dashboard",
                    SUPER_ADMIN_USER, "management/dashboard",
                    AUDITOR, "management/dashboard"
            );

    @Value("${ifs.live.projects.landing.page.url}")
    private String liveProjectsLandingPageUrl;

    @Value("${ifs.web.baseURL}")
    private String ifsWebBaseURL;

    private NavigationUtils() {}

    public String getLiveProjectsLandingPageUrl() {
        return liveProjectsLandingPageUrl;
    }

    public String getRedirectToDashboardUrlForRole(Role role) {

        if (LIVE_PROJECTS_USER.equals(role)) {
            return "redirect:" + liveProjectsLandingPageUrl;
        }

        String roleUrl = DEFAULT_LANDING_PAGE_URLS_FOR_ROLES.get(role);

        return format("redirect:/%s", hasText(roleUrl) ? roleUrl : "dashboard");
    }

    public String getDirectDashboardUrlForRole(Role role) {

        if (LIVE_PROJECTS_USER.equals(role)) {
            return liveProjectsLandingPageUrl;
        }

        String roleUrl = DEFAULT_LANDING_PAGE_URLS_FOR_ROLES.get(role);

        return getDirectToSameDomainUrl(roleUrl);
    }

    public String getRedirectToLandingPageUrl() {
        return String.format("redirect:%s", ifsWebBaseURL);
    }

    public String getDirectLandingPageUrl() {
        return ifsWebBaseURL;
    }

    public String getRedirectToSameDomainUrl(String url) {
        return String.format("redirect:%s/%s", ifsWebBaseURL, url);
    }

    public String getDirectToSameDomainUrl(String url) {
        return String.format("%s/%s", ifsWebBaseURL, url);
    }
}