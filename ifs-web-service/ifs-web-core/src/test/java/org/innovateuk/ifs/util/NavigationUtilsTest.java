package org.innovateuk.ifs.util;

import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NavigationUtilsTest {

    @Test
    public void getRedirectToDashboardUrlForApplicantRole() throws Exception{

        Role applicant = Role.APPLICANT;
        assertEquals("redirect:/applicant/dashboard", new NavigationUtils().getRedirectToDashboardUrlForRole(applicant));
    }

    @Test
    public void getRedirectToDashboardUrlForAssessorRole() throws Exception{

        Role assessor = Role.ASSESSOR;
        assertEquals("redirect:/assessment/assessor/dashboard", new NavigationUtils().getRedirectToDashboardUrlForRole(assessor));
    }

    @Test
    public void getRedirectToDashboardUrlForStakeholderRole() throws Exception{

        Role stakeholder = Role.STAKEHOLDER;
        assertEquals("redirect:/management/dashboard", new NavigationUtils().getRedirectToDashboardUrlForRole(stakeholder));
    }

    @Test
    public void getRedirectToLandingPageUrl() throws Exception{

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("site");
        when(request.getServerPort()).thenReturn(8080);

        //String.format("redirect:%s://%s:%s"
        assertEquals("redirect:https://site:8080", new NavigationUtils().getRedirectToLandingPageUrl(request));

    }

}