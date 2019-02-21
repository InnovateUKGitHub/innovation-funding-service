package org.innovateuk.ifs.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;

import static junit.framework.TestCase.assertEquals;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class NavigationUtilsTest {

    private static final String landingPageUrl = "https://site/live-projects-landing-page";

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private NavigationUtils navigationUtils;

    @Before
    public void setup() {

        ReflectionTestUtils.setField(navigationUtils, "liveProjectsLandingPageUrl", landingPageUrl);

        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("site");
        when(request.getServerPort()).thenReturn(8080);
    }

    @Test
    public void getRedirectToDashboardUrlForLiveProjectUser() throws Exception {

        assertEquals("redirect:https://site/live-projects-landing-page",
                navigationUtils.getRedirectToDashboardUrlForRole(LIVE_PROJECTS_USER));
    }

    @Test
    public void getRedirectToDashboardUrlForApplicantRole() throws Exception {

        assertEquals("redirect:/applicant/dashboard", navigationUtils.getRedirectToDashboardUrlForRole(APPLICANT));
    }

    @Test
    public void getRedirectToDashboardUrlForNoLandingPageRole() throws Exception {

        assertEquals("redirect:/dashboard", navigationUtils.getRedirectToDashboardUrlForRole(COMP_EXEC));
    }

    @Test
    public void getDirectDashboardUrlForApplicantRole() throws Exception {

        assertEquals("https://site:8080/applicant/dashboard", navigationUtils.getDirectDashboardUrlForRole(request, APPLICANT));
    }

    @Test
    public void getRedirectToLandingPageUrl() throws Exception {

        assertEquals("redirect:https://site:8080", navigationUtils.getRedirectToLandingPageUrl(request));
    }

    @Test
    public void getDirectLandingPageUrl() throws Exception {

        assertEquals("https://site:8080", navigationUtils.getDirectLandingPageUrl(request));
    }

    @Test
    public void getRedirectToSameDomainUrl() throws Exception {
        String url = "management/dashboard";

        assertEquals("redirect:https://site:8080/" + url, navigationUtils.getRedirectToSameDomainUrl(request, url));
    }

}