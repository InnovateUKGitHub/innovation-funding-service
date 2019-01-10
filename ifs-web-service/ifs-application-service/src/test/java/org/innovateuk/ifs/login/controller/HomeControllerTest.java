package org.innovateuk.ifs.login.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.CookieUtil;
import org.innovateuk.ifs.util.NavigationUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.innovateuk.ifs.CookieTestUtil.setupCookieUtil;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class HomeControllerTest extends BaseControllerMockMVCTest<HomeController> {

    @Mock
    private CookieUtil cookieUtil;

    @Spy
    private NavigationUtils navigationUtils;

    @Override
    protected HomeController supplyControllerUnderTest() {
        return new HomeController();
    }

    @Before
    public void setUpCookies() {
        setupCookieUtil(cookieUtil);
    }

    /**
     * Test if you are redirected to the login page, when you visit the root url http://<domain>/
     */
    @Test
    public void homeWithNullLoggedInUser() throws Exception {

        setLoggedInUser(null);

        mockMvc.perform(get("/"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void homeEmptyAuth() throws Exception {
        setLoggedInUserAuthentication(null);

        mockMvc.perform(get("/"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void homeNotAuthenticated() throws Exception {
        UserAuthentication userAuth = new UserAuthentication(null);
        userAuth.setAuthenticated(false);
        setLoggedInUserAuthentication(userAuth);

        mockMvc.perform(get("/"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void homeLoggedInApplicant() throws Exception {
        setLoggedInUser(applicant);

        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/applicant/dashboard"));
    }

    @Test
    public void homeLoggedInAssessor() throws Exception {
        setLoggedInUser(assessor);

        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/assessment/assessor/dashboard"));
    }

    @Test
    public void homeLoggedInStakeholder() throws Exception {
        setLoggedInUser(stakeholder);

        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/management/dashboard"));
    }

    @Test
    public void homeLoggedInWithoutRoles() throws Exception {
        setLoggedInUser(new UserResource());

        mockMvc.perform(get("/"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void homeLoggedInDualRoleAssessor() throws Exception {
        setLoggedInUser(assessorAndApplicant);

        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/dashboard-selection"));
    }

    @Test
    public void homeLoggedInMultipleRoleStakeholder() throws Exception {
        setLoggedInUser(assessorAndApplicantAndStakeholder);

        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/dashboard-selection"));
    }

    @Test
    public void homeLoggedInDualRoleInnovationLead() throws Exception {
        setLoggedInUser(innovationLeadAndApplicant);

        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/dashboard-selection"));
    }

    @Test
    public void dashboardSelection() throws Exception {
        setLoggedInUser(stakeholderAndAssessor);

        mockMvc.perform(get("/dashboard-selection"))
                .andExpect(status().isOk())
                .andExpect(view().name("login/multiple-dashboard-choice"));
    }

    @Test
    public void dashboardSelectionNotAuthenticated() throws Exception {
        UserAuthentication userAuth = new UserAuthentication(null);
        userAuth.setAuthenticated(false);
        setLoggedInUserAuthentication(userAuth);

        mockMvc.perform(get("/dashboard-selection"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    public void dashboardSelectionWithSingleRoleUser() throws Exception {
        setLoggedInUser(applicant);

        mockMvc.perform(get("/dashboard-selection"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }
}
