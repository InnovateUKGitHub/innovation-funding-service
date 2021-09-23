package org.innovateuk.ifs.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static junit.framework.TestCase.assertEquals;
import static org.innovateuk.ifs.user.resource.Role.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class NavigationUtilsTest {

    private static final String landingPageUrl = "https://site/live-projects-landing-page";

    @InjectMocks
    private NavigationUtils navigationUtils;

    @Before
    public void setup() {
        String ifsWebBaseURL = "https://site:8080";
        ReflectionTestUtils.setField(navigationUtils, "liveProjectsLandingPageUrl", landingPageUrl);
        ReflectionTestUtils.setField(navigationUtils, "ifsWebBaseURL", ifsWebBaseURL);
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
    public void getRedirectToDashboardUrlForNullRole() {
        assertEquals("redirect:/dashboard", navigationUtils.getRedirectToDashboardUrlForRole(null));
    }

    @Test
    public void getDirectDashboardUrlForApplicantRole() {
        assertEquals("https://site:8080/applicant/dashboard", navigationUtils.getDirectDashboardUrlForRole(APPLICANT));
    }

    @Test
    public void getRedirectToLandingPageUrl() {
        assertEquals("redirect:https://site:8080", navigationUtils.getRedirectToLandingPageUrl());
    }

    @Test
    public void getDirectLandingPageUrl() {
        assertEquals("https://site:8080", navigationUtils.getDirectLandingPageUrl());
    }

    @Test
    public void getDirectDashboardUrlForKnowledgeTransferAdvisor() {
        assertEquals("https://site:8080/assessment/assessor/dashboard", navigationUtils.getDirectDashboardUrlForRole(KNOWLEDGE_TRANSFER_ADVISER));
    }

    @Test
    public void getDirectDashboardUrlForSupporter() {
        assertEquals("https://site:8080/assessment/supporter/dashboard", navigationUtils.getDirectDashboardUrlForRole(SUPPORTER));
    }

    @Test
    public void getDirectDashboardUrlForAuditorRole() {
        System.out.println("Auditor" + navigationUtils.getDirectDashboardUrlForRole(AUDITOR));
        assertEquals("https://site:8080/management/dashboard", navigationUtils.getDirectDashboardUrlForRole(AUDITOR));
    }

    @Test
    public void getRedirectToSameDomainUrl() {
        String url = "management/dashboard";
        assertEquals("redirect:https://site:8080/" + url, navigationUtils.getRedirectToSameDomainUrl(url));
    }

}