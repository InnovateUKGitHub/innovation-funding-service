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
    public void getRedirectToDashboardUrlForLiveProjectUser() {
        assertEquals("redirect:https://site/live-projects-landing-page",
                navigationUtils.getRedirectToDashboardUrlForRole(LIVE_PROJECTS_USER));
    }

    @Test
    public void getRedirectToDashboardUrlForApplicantRole() {
        assertEquals("redirect:/applicant/dashboard", navigationUtils.getRedirectToDashboardUrlForRole(APPLICANT));
    }

    @Test
    public void getRedirectToDashboardUrlForNoLandingPageRole() {
        assertEquals("redirect:/dashboard", navigationUtils.getRedirectToDashboardUrlForRole(COMP_EXEC));
    }

    @Test
    public void getRedirectToDashboardUrlForNullRole() {
        assertEquals("redirect:/dashboard", navigationUtils.getRedirectToDashboardUrlForRole(null));
    }

    @Test
    public void getDirectDashboardUrlForApplicantRole() {
        assertEquals("https://site:8080/applicant/dashboard", navigationUtils.getDirectDashboardUrlForRole(request, APPLICANT));
    }

    @Test
    public void getRedirectToLandingPageUrl() {
        assertEquals("redirect:https://site:8080", navigationUtils.getRedirectToLandingPageUrl(request));
    }

    @Test
    public void getDirectLandingPageUrl() {
        assertEquals("https://site:8080", navigationUtils.getDirectLandingPageUrl(request));
    }

    @Test
    public void getDirectDashboardUrlForKnowledgeTransferAdvisor() {
        assertEquals("https://site:8080/assessment/assessor/dashboard", navigationUtils.getDirectDashboardUrlForRole(request, KNOWLEDGE_TRANSFER_ADVISER));
    }

    @Test
    public void getDirectDashboardUrlForSupporter() {
        assertEquals("https://site:8080/assessment/supporter/dashboard", navigationUtils.getDirectDashboardUrlForRole(request, SUPPORTER));
    }

    @Test
    public void getRedirectToSameDomainUrl() {
        String url = "management/dashboard";

        assertEquals("redirect:https://site:8080/" + url, navigationUtils.getRedirectToSameDomainUrl(request, url));
    }

}