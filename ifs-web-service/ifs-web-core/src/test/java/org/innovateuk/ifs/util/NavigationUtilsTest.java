package org.innovateuk.ifs.util;

import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class NavigationUtilsTest {

    @Test
    public void getRedirectToDashboardUrlForApplicantRole(){

        Role applicant = Role.APPLICANT;
        assertEquals("redirect:/applicant/dashboard", new NavigationUtils().getRedirectToDashboardUrlForRole(applicant));
    }

    @Test
    public void getRedirectToDashboardUrlForAssessorRole(){

        Role assessor = Role.ASSESSOR;
        assertEquals("redirect:/assessment/assessor/dashboard", new NavigationUtils().getRedirectToDashboardUrlForRole(assessor));
    }

    @Test
    public void getRedirectToDashboardUrlForStakeholderRole(){

        Role stakeholder = Role.STAKEHOLDER;
        assertEquals("redirect:/management/dashboard", new NavigationUtils().getRedirectToDashboardUrlForRole(stakeholder));
    }

}