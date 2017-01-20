package org.innovateuk.ifs.project.eligibility.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.finance.view.ProjectFinanceOverviewModelManager;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.populator.ApplicationNavigationPopulator;
import org.innovateuk.ifs.application.populator.ApplicationSectionAndQuestionModelPopulator;
import org.innovateuk.ifs.application.populator.OpenProjectFinanceSectionModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.project.eligibility.viewmodel.FinanceChecksEligibilityViewModel;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationSize;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.Model;

import java.util.HashSet;
import java.util.Map;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.service.Futures.settable;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.rest.ValidationMessages.noErrors;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckEligibilityResourceBuilder.newFinanceCheckEligibilityResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FinanceCheckEligibilityControllerTest extends BaseControllerMockMVCTest<FinanceChecksEligibilityController> {

    @Spy
    @InjectMocks
    private ProjectFinanceOverviewModelManager projectFinanceOverviewModelManager;

    @Spy
    @InjectMocks
    private OpenProjectFinanceSectionModelPopulator openFinanceSectionModel;

    @Mock
    private ApplicationModelPopulator applicationModelPopulator;

    @Mock
    private ApplicationSectionAndQuestionModelPopulator applicationSectionAndQuestionModelPopulator;

    @Mock
    private ApplicationNavigationPopulator applicationNavigationPopulator;

    @Mock
    private Model model;

    private OrganisationResource industrialOrganisation = newOrganisationResource().
            withName("Industrial Org").
            withOrganisationSize(OrganisationSize.MEDIUM).
            withCompanyHouseNumber("123456789").
            withOrganisationTypeName("Business").
            build();

    private OrganisationResource academicOrganisation = newOrganisationResource().
            withName("Academic Org").
            withOrganisationSize(OrganisationSize.LARGE).
            build();

    private ApplicationResource application = newApplicationResource().withId(123L).build();

    private ProjectResource project = newProjectResource().withName("Project1").withApplication(application).build();

    private FinanceCheckEligibilityResource eligibility = newFinanceCheckEligibilityResource().build();

    @Before
    public void setUp() {

        super.setUp();

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.loginDefaultUser();
        this.setupUserRoles();
        this.setupFinances();
        this.setupInvites();
        this.setupQuestionStatus(applications.get(0));

        application = applications.get(0);
        project.setApplication(application.getId());

        // save actions should always succeed.
        when(formInputResponseService.save(anyLong(), anyLong(), anyLong(), eq(""), anyBoolean())).thenReturn(new ValidationMessages(fieldError("value", "", "Please enter some text 123")));
        when(formInputResponseService.save(anyLong(), anyLong(), anyLong(), anyString(), anyBoolean())).thenReturn(noErrors());

        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));
        when(sectionService.getAllByCompetitionId(anyLong())).thenReturn(sectionResources);
        when(applicationService.getById(application.getId())).thenReturn(application);

        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getByApplicationId(application.getId())).thenReturn(project);
        when(organisationService.getOrganisationById(industrialOrganisation.getId())).thenReturn(industrialOrganisation);
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(industrialOrganisation);
        when(financeCheckServiceMock.getFinanceCheckEligibilityDetails(project.getId(), industrialOrganisation.getId())).thenReturn(eligibility);
        when(financeHandler.getProjectFinanceModelManager("Business")).thenReturn(defaultProjectFinanceModelManager);
    }

    @Test
    public void testViewEligibilityLeadOrg() throws Exception {
        MvcResult result = mockMvc.perform(get("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility",
                project.getId(), industrialOrganisation.getId())).
                andExpect(status().isOk()).
                andExpect(model().attributeExists("model")).
                andExpect(view().name("project/financecheck/eligibility")).
                andReturn();

        Map<String, Object> model = result.getModelAndView().getModel();

        FinanceChecksEligibilityViewModel viewModel = (FinanceChecksEligibilityViewModel) model.get("model");

        assertTrue(viewModel.isLeadPartnerOrganisation());
        assertTrue(viewModel.getApplicationId().equals(application.getFormattedId()));
        assertTrue(viewModel.getOrganisationName().equals(industrialOrganisation.getName()));
        assertTrue(viewModel.getProjectName().equals(project.getName()));

    }

    @Test
    public void testViewEligibilityNonLeadOrg() throws Exception {

        when(projectService.getLeadOrganisation(project.getId())).thenReturn(academicOrganisation);

        MvcResult result = mockMvc.perform(get("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility",
                project.getId(), industrialOrganisation.getId())).
                andExpect(status().isOk()).
                andExpect(model().attributeExists("model")).
                andExpect(view().name("project/financecheck/eligibility")).
                andReturn();

        Map<String, Object> model = result.getModelAndView().getModel();

        FinanceChecksEligibilityViewModel viewModel = (FinanceChecksEligibilityViewModel) model.get("model");

        assertFalse(viewModel.isLeadPartnerOrganisation());
        assertTrue(viewModel.getApplicationId().equals(application.getFormattedId()));
        assertTrue(viewModel.getOrganisationName().equals(industrialOrganisation.getName()));
        assertTrue(viewModel.getProjectName().equals(project.getName()));

    }

    @Override
    protected FinanceChecksEligibilityController supplyControllerUnderTest() {
        return new FinanceChecksEligibilityController();
    }
}