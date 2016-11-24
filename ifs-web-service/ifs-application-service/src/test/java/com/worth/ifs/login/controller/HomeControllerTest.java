package com.worth.ifs.login.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.commons.security.authentication.user.UserAuthentication;
import com.worth.ifs.login.HomeController;
import com.worth.ifs.login.form.RoleSelectionForm;
import com.worth.ifs.login.model.RoleSelectionModelPopulator;
import com.worth.ifs.login.viewmodel.RoleSelectionViewModel;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;


public class HomeControllerTest extends BaseControllerMockMVCTest<HomeController> {

    @Spy
    @InjectMocks
    private RoleSelectionModelPopulator roleSelectionModelPopulator;

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
        setLoggedInUser(applicant);

        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/applicant/dashboard"));
    }

    @Test
    public void testHomeLoggedInAssessor() throws Exception {
        setLoggedInUser(assessor);

        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/assessor/dashboard"));
    }

    @Test
    public void testHomeLoggedInWithoutRoles() throws Exception {
        setLoggedInUser(new UserResource());

        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/dashboard"));
    }

    @Test
    public void testHomeLoggedInDualRoleAssessor() throws Exception {
        setLoggedInUser(assessorAndApplicant);

        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/roleSelection"));
    }

    @Test
    public void testRoleChoice() throws Exception {
        setLoggedInUser(assessorAndApplicant);

        mockMvc.perform(get("/roleSelection"))
                .andExpect(status().isOk())
                .andExpect(view().name("/login/dual-user-choice"));
    }

    @Test
    public void testRoleChoiceNotAuthenticated() throws Exception {
        UserAuthentication userAuth = new UserAuthentication(null);
        userAuth.setAuthenticated(false);
        setLoggedInUserAuthentication(userAuth);

        mockMvc.perform(get("/roleSelection"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    public void testRoleSelectionWithSingleRoleUser() throws Exception {
        setLoggedInUser(applicant);

        mockMvc.perform(get("/roleSelection"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    public void testRoleSelectionAssessor() throws Exception {
        setLoggedInUser(assessorAndApplicant);
        UserRoleType selectedRole = UserRoleType.ASSESSOR;

        mockMvc.perform(post("/roleSelection")
                .param("selectedRole", selectedRole.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assessor/dashboard"))
                .andReturn();
    }

    @Test
    public void testRoleSelectionApplicant() throws Exception {
        setLoggedInUser(assessorAndApplicant);
        UserRoleType selectedRole = UserRoleType.APPLICANT;

        mockMvc.perform(post("/roleSelection")
                .param("selectedRole", selectedRole.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/applicant/dashboard"))
                .andReturn();
    }

    @Test
    public void testRoleNotSelected() throws Exception {
        setLoggedInUser(assessorAndApplicant);
        RoleSelectionForm expectedForm = new RoleSelectionForm();
        expectedForm.setSelectedRole(null);

        MvcResult result = mockMvc.perform(post("/roleSelection"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "selectedRole"))
                .andExpect(view().name("/login/dual-user-choice"))
                .andReturn();

        RoleSelectionForm form = (RoleSelectionForm) result.getModelAndView().getModel().get("form");
        assertEquals(null, form.getSelectedRole());

        BindingResult bindingResult = form.getBindingResult();
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("selectedRole"));
        assertEquals("Please select a role", bindingResult.getFieldError("selectedRole").getDefaultMessage());
    }
}