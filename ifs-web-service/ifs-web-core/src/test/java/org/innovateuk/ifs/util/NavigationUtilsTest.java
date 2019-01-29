package org.innovateuk.ifs.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;

import static org.innovateuk.ifs.user.resource.Role.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NavigationUtilsTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private NavigationUtils navigationUtils;

    private static final String liveProjectsLandingPage = "https://ifs-local.dev/live-projects-landing-page";

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(navigationUtils, "liveProjectsLandingPageUrl", liveProjectsLandingPage);

        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("ifs.local");
        when(request.getServerPort()).thenReturn(8080);
    }

    @Test
    public void liveProjectUserRedirectToLandingPage() {
        String result = navigationUtils.getRedirectToDashboardUrlForRole(LIVE_PROJECTS_USER);
        assertEquals( "redirect:" + liveProjectsLandingPage, result);
    }

    @Test
    public void getRedirectToDashboardUrlForApplicantRole() {
        String result = navigationUtils.getRedirectToDashboardUrlForRole(APPLICANT);
        assertEquals("redirect:/applicant/dashboard", result);
    }

    @Test
    public void noLandingPageUrlRedirectsToDashboard() {
        String result = navigationUtils.getRedirectToDashboardUrlForRole(COMP_EXEC);
        assertEquals("redirect:/dashboard", result);
    }

    @Test
    public void getDirectDashboardUrlForApplicantRole() {
        String result = navigationUtils.getDirectDashboardUrlForRole(request, APPLICANT);
        assertEquals("https://ifs.local:8080/applicant/dashboard", result);
    }

    @Test
    public void getRedirectToSameDomainUrl() {
        String url = "my-endpoint";
        String result = navigationUtils.getRedirectToSameDomainUrl(request, url);
        assertEquals("redirect:https://ifs.local:8080/" + url, result);
    }

    @Test
    public void getDirectLandingPageUrl() {
        String result = navigationUtils.getDirectLandingPageUrl(request);
        assertEquals("https://ifs.local:8080", result);
    }

    @Test
    public void getRedirectToLandingPageUrl() {
        String result = navigationUtils.getRedirectToLandingPageUrl(request);
        assertEquals("redirect:https://ifs.local:8080", result);
    }
}