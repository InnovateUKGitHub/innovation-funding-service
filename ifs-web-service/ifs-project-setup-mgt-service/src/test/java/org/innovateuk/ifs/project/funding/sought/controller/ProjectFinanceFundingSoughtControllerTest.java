package org.innovateuk.ifs.project.funding.sought.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.GrantClaimAmount;
import org.innovateuk.ifs.finance.service.ProjectFinanceRowRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.funding.sought.viewmodel.ProjectFinanceFundingSoughtViewModel;
import org.innovateuk.ifs.project.funding.sought.viewmodel.ProjectFinancePartnerFundingSoughtViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProjectFinanceFundingSoughtControllerTest extends BaseControllerMockMVCTest<ProjectFinanceFundingSoughtController> {

    private static final long projectId = 1L;
    private static final long industrialOrganisation = 2L;
    private static final long academicOrganisation = 3L;
    private static final ProjectFinanceResource industrialFinances = newProjectFinanceResource()
            .withOrganisation(industrialOrganisation)
            .withIndustrialCosts()
            .withGrantClaimAmount(new BigDecimal(999))
            .build();
    private static final ProjectFinanceResource academicFinances = newProjectFinanceResource()
            .withOrganisation(academicOrganisation)
            .withAcademicCosts()
            .withGrantClaimAmount(new BigDecimal(999))
            .build();
    private static final ProjectResource project = newProjectResource()
            .withId(projectId)
            .withName("Project")
            .withApplication(5L)
            .build();

    @Mock
    private ProjectRestService projectRestService;

    @Mock
    private ProjectFinanceRestService projectFinanceRestService;

    @Mock
    private ProjectFinanceRowRestService financeRowRestService;

    @Override
    protected ProjectFinanceFundingSoughtController supplyControllerUnderTest() {
        return new ProjectFinanceFundingSoughtController();
    }

    @Test
    public void viewFundingLevels() throws Exception {
        when(projectFinanceRestService.getProjectFinances(projectId)).thenReturn(restSuccess(asList(industrialFinances, academicFinances)));
        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(project));

        MvcResult result = mockMvc.perform(get("/project/{projectId}/funding-sought", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/financecheck/funding-sought"))
                .andReturn();

        ProjectFinanceFundingSoughtViewModel viewModel = (ProjectFinanceFundingSoughtViewModel) result.getModelAndView().getModel().get("model");

        BigDecimal totalGrant = industrialFinances.getTotalFundingSought().add(academicFinances.getTotalFundingSought());
        assertEquals("Project", viewModel.getProjectName());
        assertEquals(5L, viewModel.getApplicationId());
        assertEquals(projectId, viewModel.getProjectId());
        assertEquals(2, viewModel.getPartners().size());

        ProjectFinancePartnerFundingSoughtViewModel industrialViewModel = viewModel.getPartners().get(0);
        assertEquals(industrialOrganisation, industrialViewModel.getId());

        ProjectFinancePartnerFundingSoughtViewModel academicViewModel = viewModel.getPartners().get(1);
        assertEquals(academicOrganisation, academicViewModel.getId());
    }

    @Test
    public void saveFundingLevels_success() throws Exception {
        when(projectFinanceRestService.getProjectFinances(projectId)).thenReturn(restSuccess(asList(industrialFinances, academicFinances)));
        when(financeRowRestService.update(any())).thenReturn(restSuccess(ValidationMessages.noErrors()));

        mockMvc.perform(post("/project/{projectId}/funding-sought", projectId)
                .param(format("partners[%d].funding", industrialOrganisation), "1000")
                .param(format("partners[%d].funding", academicOrganisation), "2000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/project/%d/finance-check-overview", projectId)))
                .andReturn();

        verify(financeRowRestService).update(academicFinances.getGrantClaim());
        verify(financeRowRestService).update(industrialFinances.getGrantClaim());

        assertEquals(new BigDecimal(1000), ((GrantClaimAmount) industrialFinances.getGrantClaim()).getAmount());
        assertEquals(new BigDecimal(2000), ((GrantClaimAmount) academicFinances.getGrantClaim()).getAmount());
    }
}
