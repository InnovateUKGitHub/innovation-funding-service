package com.worth.ifs.login.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.commons.security.UserAuthentication;
import com.worth.ifs.login.HomeController;
import com.worth.ifs.login.RoleSelectionForm;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;

import static java.lang.String.format;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;


public class HomeControllerTest extends BaseControllerMockMVCTest<HomeController> {

    @Override
    protected HomeController supplyControllerUnderTest() {
        return new HomeController();
    }

    @Before
    public void setUp() {
        super.setUp();
    }

    /**
     * Test if you are redirected to the login page, when you visit the root url http://<domain>/
     */
    @Test
    public void testHome() throws Exception {

        setLoggedInUser(null);

        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    public void testHomeEmptyAuth() throws Exception {
        setLoggedInUserAuthentication(null);

        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    public void testHomeNotAuthenticated() throws Exception {
        UserAuthentication userAuth = new UserAuthentication(null);
        userAuth.setAuthenticated(false);
        setLoggedInUserAuthentication(userAuth);

        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    public void testHomeLoggedInApplicant() throws Exception {
        this.setup();
        setLoggedInUser(applicant);

        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/applicant/dashboard"));
    }

    @Test
    public void testHomeLoggedInAssessor() throws Exception {
        this.setup();
        setLoggedInUser(assessor);

        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/assessor/dashboard"));
    }

    @Test
    public void testHomeLoggedInWithoutRoles() throws Exception {
        this.setup();
        setLoggedInUser(new UserResource());

        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/dashboard"));
    }

    @Test
    public void testHomeLoggedInDualRoleAssessor() throws Exception {
        this.setup();
        setLoggedInUser(assessorAndApplicant);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("/role"));
    }

    @Test
    public void testRoleChoice() throws Exception {
        this.setup();
        setLoggedInUser(assessorAndApplicant);

        mockMvc.perform(get("/role"))
                .andExpect(status().isOk())
                .andExpect(view().name("/roleSelection"));
    }

    @Test
    public void testRoleChoiceNotAuthenticated() throws Exception {

        UserAuthentication userAuth = new UserAuthentication(null);
        userAuth.setAuthenticated(false);
        setLoggedInUserAuthentication(userAuth);

        mockMvc.perform(get("/role"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    public void testRoleSelectionWithSingleRoleUser() throws Exception {
        //this.setup();
        setLoggedInUser(applicant);

        mockMvc.perform(get("/role"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    public void testRoleSelectionAssessor() throws Exception {
        //this.setup();
        setLoggedInUser(assessorAndApplicant);
        UserRoleType selectedRole = UserRoleType.ASSESSOR;

        mockMvc.perform(post("/role")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("selectedRole", selectedRole.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/assessor/dashboard")))
                .andReturn();
    }

    @Test
    public void testRoleSelectionApplicant() throws Exception {
        //this.setup();
        setLoggedInUser(assessorAndApplicant);
        UserRoleType selectedRole = UserRoleType.APPLICANT;

        mockMvc.perform(post("/role")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("selectedRole", selectedRole.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/applicant/dashboard")))
                .andReturn();
    }

    @Test
    public void testRoleNotSelected() throws Exception {
       // this.setup();
        setLoggedInUser(assessorAndApplicant);
        RoleSelectionForm expectedForm = new RoleSelectionForm();
        expectedForm.setSelectedRole(null);

        mockMvc.perform(post("/role")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
              //  .andExpect(model().attributeExists("model"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "selectedRole"))
                .andExpect(view().name("/role"))
                .andReturn();
    }
}