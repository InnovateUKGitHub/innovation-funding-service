package org.innovateuk.ifs.util;

import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NavigationUtilsTest {

    @Test
    public void getRedirectToDashboardUrlForLiveProjectUser() throws Exception {

        String landingPageUrl = "https://site/live-projects-landing-page";

        NavigationUtils navigationUtils = new NavigationUtils();
        ReflectionTestUtils.setField(navigationUtils, "liveProjectsLandingPageUrl", landingPageUrl);

        assertEquals("redirect:https://site/live-projects-landing-page",
                navigationUtils.getRedirectToDashboardUrlForRole(Role.LIVE_PROJECTS_USER));
    }

    @Test
    public void getRedirectToDashboardUrlForApplicantRole() throws Exception {

        assertEquals("redirect:/applicant/dashboard",
                new NavigationUtils().getRedirectToDashboardUrlForRole(Role.APPLICANT));
    }

    @Test
    public void getRedirectToDashboardUrlForAssessorRole() throws Exception {

        assertEquals("redirect:/assessment/assessor/dashboard",
                new NavigationUtils().getRedirectToDashboardUrlForRole(Role.ASSESSOR));
    }

    @Test
    public void getRedirectToDashboardUrlForStakeholderRole() throws Exception {

        assertEquals("redirect:/management/dashboard",
                new NavigationUtils().getRedirectToDashboardUrlForRole(Role.STAKEHOLDER));
    }

    @Test
    public void getDirectDashboardUrlForRole() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("site");
        when(request.getServerPort()).thenReturn(8080);

        assertEquals("https://site:8080/applicant/dashboard",
                new NavigationUtils().getDirectDashboardUrlForRole(request, Role.APPLICANT));
    }

    @Test
    public void getRedirectToLandingPageUrl() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("site");
        when(request.getServerPort()).thenReturn(8080);

        //String.format("redirect:%s://%s:%s"
        assertEquals("redirect:https://site:8080", new NavigationUtils().getRedirectToLandingPageUrl(request));
    }

    @Test
    public void getDirectLandingPageUrl() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("site");
        when(request.getServerPort()).thenReturn(8080);

        assertEquals("https://site:8080", new NavigationUtils().getDirectLandingPageUrl(request));
    }

    @Test
    public void getRedirectToSameDomainUrl() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("site");
        when(request.getServerPort()).thenReturn(8080);

        //String.format("redirect:%s://%s:%s/%s"
        assertEquals("redirect:https://site:8080/management/dashboard",
                new NavigationUtils().getRedirectToSameDomainUrl(request, "management/dashboard"));
    }

}