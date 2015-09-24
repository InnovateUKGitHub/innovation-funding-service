package com.worth.ifs.application.controller;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.application.ApplicationFormController;
import com.worth.ifs.application.finance.CostCategory;
import com.worth.ifs.application.finance.CostType;
import com.worth.ifs.user.domain.ProcessRole;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.EnumMap;
import java.util.regex.Pattern;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;



@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations="classpath:application.properties")
public class ApplicationFormControllerTest  extends BaseUnitTest {

    @InjectMocks
    private ApplicationFormController applicationFormController;

    private static ResultMatcher matchUrl(final String expectedString) {
        return new ResultMatcher() {
            public void match(MvcResult result) {
                assertTrue(result.getResponse().getRedirectedUrl().equals(expectedString));
            }
        };
    }

    @Before
    public void setUp(){
        super.setup();

        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(applicationFormController)
                .setViewResolvers(viewResolver())
                .build();

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.loginDefaultUser();
        this.setupFinances();
    }

    @Test
    public void testApplicationForm() throws Exception {
        com.worth.ifs.application.domain.Application app = applications.get(0);
        ProcessRole userAppRole = new ProcessRole();

        //when(applicationService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(applications);
        when(applicationService.getById(app.getId())).thenReturn(app);
        when(processRoleService.findProcessRole(loggedInUser.getId(), app.getId())).thenReturn(userAppRole);

        mockMvc.perform(get("/application-form/1"))
                .andExpect(view().name("application-form"))
                .andExpect(model().attribute("currentApplication", app))
                .andExpect(model().attribute("currentSectionId", 0L));

    }

    @Test
    public void testApplicationFormWithOpenSection() throws Exception {
        Application app = applications.get(0);
        EnumMap<CostType, CostCategory> costCategories = new EnumMap<>(CostType.class);

        //when(applicationService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(applications);
        when(applicationService.getById(app.getId())).thenReturn(app);

        mockMvc.perform(get("/application-form/1/section/1"))
                .andExpect(view().name("application-form"))
                .andExpect(model().attribute("currentApplication", app))
                .andExpect(model().attribute("leadOrganisation", organisations.get(0)))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasSize(organisations.size())))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasItem(organisations.get(0))))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasItem(organisations.get(1))))
                .andExpect(model().attribute("userIsLeadApplicant", true))
                .andExpect(model().attribute("currentSectionId", 1L));

    }

    @Test
    public void costControllerShouldRedirectToCorrectLocationAfterCostDelete() throws Exception {
        Long applicationId = 10L;
        Long sectionId =20L;
        Long costId = 30L;

        doNothing().when(costService).delete(costId);

        mockMvc.perform(get("/application-form/deletecost/"+applicationId+"/"+sectionId+"/0"))
                .andExpect(matchUrl("/application-form/10/section/20"));
    }

    @Test
    public void testAddAnother() throws Exception {

    }

    @Test
    public void testApplicationFormSubmit() throws Exception {

    }

    @Test
    public void testSaveFormElement() throws Exception {

    }
}