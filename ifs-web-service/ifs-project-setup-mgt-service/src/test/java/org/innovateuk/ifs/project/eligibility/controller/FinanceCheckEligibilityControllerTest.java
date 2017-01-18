package org.innovateuk.ifs.project.eligibility.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.eligibility.viewmodel.FinanceChecksEligibilityViewModel;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationSize;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckEligibilityResourceBuilder.newFinanceCheckEligibilityResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class FinanceCheckEligibilityControllerTest extends BaseControllerMockMVCTest<FinanceChecksEligibilityController> {

    private OrganisationResource industrialOrganisation = newOrganisationResource().
            withName("Industrial Org").
            withOrganisationSize(OrganisationSize.MEDIUM).
            withCompanyHouseNumber("123456789").
            build();

    private OrganisationResource academicOrganisation = newOrganisationResource().
            withName("Academic Org").
            withOrganisationSize(OrganisationSize.LARGE).
            build();

    private ApplicationResource application = newApplicationResource().withId(123L).build();

    private ProjectResource project = newProjectResource().withName("Project1").withApplication(application).build();

    private FinanceCheckEligibilityResource eligibility = newFinanceCheckEligibilityResource().build();

    @Test
    public void testViewEligibilityLeadOrg() throws Exception {

        when(projectService.getById(project.getId())).thenReturn(project);
        when(applicationService.getById(application.getId())).thenReturn(application);
        when(organisationService.getOrganisationById(industrialOrganisation.getId())).thenReturn(industrialOrganisation);
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(industrialOrganisation);
        when(financeCheckServiceMock.getFinanceCheckEligibilityDetails(project.getId(), industrialOrganisation.getId())).thenReturn(eligibility);

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

        when(projectService.getById(project.getId())).thenReturn(project);
        when(applicationService.getById(application.getId())).thenReturn(application);
        when(organisationService.getOrganisationById(industrialOrganisation.getId())).thenReturn(industrialOrganisation);
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(academicOrganisation);
        when(financeCheckServiceMock.getFinanceCheckEligibilityDetails(project.getId(), industrialOrganisation.getId())).thenReturn(eligibility);

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
