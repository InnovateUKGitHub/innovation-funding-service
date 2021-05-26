package org.innovateuk.ifs.project.fundingrules.controller;

import org.innovateuk.ifs.AbstractAsyncWaitMockMVCTest;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.finance.builder.FundingRulesResourceBuilder;
import org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder;
import org.innovateuk.ifs.project.finance.resource.EligibilityRagStatus;
import org.innovateuk.ifs.project.finance.resource.EligibilityResource;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.project.finance.service.FinanceCheckRestService;
import org.innovateuk.ifs.project.fundingrules.populator.FinanceChecksFundingRulesViewModelPopulator;
import org.innovateuk.ifs.project.fundingrules.viewmodel.FinanceChecksFundingRulesViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.finance.builder.FundingRulesResourceBuilder.newFundingRulesResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.resource.ProjectState.SETUP;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FinanceChecksFundingRulesControllerTest extends AbstractAsyncWaitMockMVCTest<FinanceChecksFundingRulesController> {

    @Mock
    private FinanceCheckRestService financeCheckRestService;

    @Mock
    private FinanceChecksFundingRulesViewModelPopulator financeChecksFundingRulesViewModelPopulator;

    @Test
    public void testViewFundingRules() throws Exception {

        long projectId = 1L;
        long organisationId = 2L;

        FinanceChecksFundingRulesViewModel financeChecksFundingRulesViewModel = new FinanceChecksFundingRulesViewModel(
                newProjectResource().build(), null, newOrganisationResource().build(),
                false, newFundingRulesResource().build(), null, false);
        when(financeChecksFundingRulesViewModelPopulator.populateFundingRulesViewModel(projectId, organisationId, false)).thenReturn(financeChecksFundingRulesViewModel);

        MvcResult result = mockMvc.perform(get("/project/{projectId}/finance-check/organisation/{organisationId}/funding-rules",
                projectId, organisationId)).
                andExpect(status().isOk()).
                andExpect(model().attributeExists("model")).
                andExpect(view().name("project/financecheck/fundingrules")).
                andReturn();
    }

    @Test
    public void testApproveFundingRules() throws Exception {

        long projectId = 1L;
        long organisationId = 2L;

        when(financeCheckRestService.approveFundingRules(projectId, organisationId)).thenReturn(restSuccess());

        MvcResult result = mockMvc.perform(post("/project/{projectId}/finance-check/organisation/{organisationId}/funding-rules",
                projectId, organisationId).param("confirmFundingRules", "true")).
                andExpect(status().is3xxRedirection()).
                andExpect(redirectedUrl("/project/1/finance-check/organisation/2/funding-rules")).
                andReturn();

        verify(financeCheckRestService).approveFundingRules(projectId, organisationId);
    }

    @Test
    public void testApproveFundingRulesWithoutCheckingBox() throws Exception {

        long projectId = 1L;
        long organisationId = 2L;

        FinanceChecksFundingRulesViewModel financeChecksFundingRulesViewModel = new FinanceChecksFundingRulesViewModel(
                newProjectResource().build(), null, newOrganisationResource().build(),
                false, newFundingRulesResource().build(), null, false);
        when(financeChecksFundingRulesViewModelPopulator.populateFundingRulesViewModel(projectId, organisationId, false)).thenReturn(financeChecksFundingRulesViewModel);

        MvcResult result = mockMvc.perform(post("/project/{projectId}/finance-check/organisation/{organisationId}/funding-rules",
                projectId, organisationId).param("confirmFundingRules", "false")).
                andExpect(status().isOk()).
                andExpect(model().attributeExists("model")).
                andExpect(model().attributeHasFieldErrorCode("form", "confirmFundingRules", "AssertTrue")).
                andExpect(view().name("project/financecheck/fundingrules")).
                andReturn();
    }

    @Test
    public void testEditFundingRules() throws Exception {

        long projectId = 1L;
        long organisationId = 2L;

        FinanceChecksFundingRulesViewModel financeChecksFundingRulesViewModel = new FinanceChecksFundingRulesViewModel(
                newProjectResource().build(), null, newOrganisationResource().build(),
                false, newFundingRulesResource().build(), null, false);
        when(financeChecksFundingRulesViewModelPopulator.populateFundingRulesViewModel(projectId, organisationId, true)).thenReturn(financeChecksFundingRulesViewModel);

        MvcResult result = mockMvc.perform(get("/project/{projectId}/finance-check/organisation/{organisationId}/funding-rules/edit",
                projectId, organisationId)).
                andExpect(status().isOk()).
                andExpect(model().attributeExists("model")).
                andExpect(view().name("project/financecheck/fundingrules")).
                andReturn();
    }

    @Test
    public void testUpdateFundingRules() throws Exception {

        long projectId = 1L;
        long organisationId = 2L;

        when(financeCheckRestService.getFundingRules(projectId, organisationId)).thenReturn(restSuccess(newFundingRulesResource()
                .withFundingRules(FundingRules.STATE_AID).build()));

        when(financeCheckRestService.saveFundingRules(projectId, organisationId, FundingRules.SUBSIDY_CONTROL)).thenReturn(restSuccess());

        MvcResult result = mockMvc.perform(post("/project/{projectId}/finance-check/organisation/{organisationId}/funding-rules/edit",
                projectId, organisationId).param("overrideFundingRules", "true")).
                andExpect(status().is3xxRedirection()).
                andExpect(redirectedUrl("/project/1/finance-check/organisation/2/funding-rules")).
                andReturn();

        verify(financeCheckRestService).saveFundingRules(projectId, organisationId, FundingRules.SUBSIDY_CONTROL);
    }

    @Test
    public void testUpdateFundingRulesWithoutCheckingBox() throws Exception {

        long projectId = 1L;
        long organisationId = 2L;

        FinanceChecksFundingRulesViewModel financeChecksFundingRulesViewModel = new FinanceChecksFundingRulesViewModel(
                newProjectResource().build(), null, newOrganisationResource().build(),
                false, newFundingRulesResource().build(), null, false);
        when(financeChecksFundingRulesViewModelPopulator.populateFundingRulesViewModel(projectId, organisationId, true)).thenReturn(financeChecksFundingRulesViewModel);

        MvcResult result = mockMvc.perform(post("/project/{projectId}/finance-check/organisation/{organisationId}/funding-rules/edit",
                projectId, organisationId).param("overrideFundingRules", "false")).
                andExpect(status().isOk()).
                andExpect(model().attributeExists("model")).
                andExpect(model().attributeHasFieldErrorCode("form", "overrideFundingRules", "AssertTrue")).
                andExpect(view().name("project/financecheck/fundingrules")).
                andReturn();
    }

    @Override
    protected FinanceChecksFundingRulesController supplyControllerUnderTest() {
        return new FinanceChecksFundingRulesController();
    }
}