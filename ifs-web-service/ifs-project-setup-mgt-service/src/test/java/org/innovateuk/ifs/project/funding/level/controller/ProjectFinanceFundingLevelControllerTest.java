package org.innovateuk.ifs.project.funding.level.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.ProjectFinanceRowRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.funding.level.viewmodel.ProjectFinanceFundingLevelViewModel;
import org.innovateuk.ifs.project.funding.level.viewmodel.ProjectFinancePartnerFundingLevelViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.MAX_DECIMAL_PLACES;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProjectFinanceFundingLevelControllerTest extends BaseControllerMockMVCTest<ProjectFinanceFundingLevelController> {

    private static final long projectId = 1L;
    private static final long industrialOrganisation = 2L;
    private static final long academicOrganisation = 3L;
    private static final ProjectFinanceResource industrialFinances = newProjectFinanceResource()
            .withOrganisation(industrialOrganisation)
            .withIndustrialCosts()
            .withGrantClaimPercentage(BigDecimal.valueOf(50))
            .withMaximumFundingLevel(60)
            .withOrganisationSize(OrganisationSize.SMALL)
            .build();
    private static final ProjectFinanceResource academicFinances = newProjectFinanceResource()
            .withOrganisation(academicOrganisation)
            .withAcademicCosts()
            .withGrantClaimPercentage(BigDecimal.valueOf(100))
            .withMaximumFundingLevel(100)
            .build();
    private static final ApplicationFinanceResource applicationIndustrialFinances = newApplicationFinanceResource()
            .withOrganisation(industrialOrganisation)
            .withIndustrialCosts()
            .withGrantClaimPercentage(BigDecimal.valueOf(50))
            .withMaximumFundingLevel(60)
            .withOrganisationSize(OrganisationSize.SMALL)
            .build();
    private static final ApplicationFinanceResource applicationAcademicFinances = newApplicationFinanceResource()
            .withOrganisation(academicOrganisation)
            .withAcademicCosts()
            .withGrantClaimPercentage(BigDecimal.valueOf(100))
            .withMaximumFundingLevel(100)
            .build();
    private static final ProjectResource project = newProjectResource()
            .withId(projectId)
            .withName("Project")
            .withApplication(5L)
            .withCompetition(6L)
            .build();
    private static final CompetitionResource competition = newCompetitionResource().build();

    static {
        industrialFinances.getFinanceOrganisationDetails().values().forEach(FinanceRowCostCategory::calculateTotal);
        academicFinances.getFinanceOrganisationDetails().values().forEach(FinanceRowCostCategory::calculateTotal);
    }

    @Mock
    private ProjectRestService projectRestService;

    @Mock
    private ProjectFinanceRestService projectFinanceRestService;

    @Mock
    private ProjectFinanceRowRestService financeRowRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Override
    protected ProjectFinanceFundingLevelController supplyControllerUnderTest() {
        return new ProjectFinanceFundingLevelController();
    }

    @Test
    public void viewFundingLevels() throws Exception {
        when(projectFinanceRestService.getProjectFinances(projectId)).thenReturn(restSuccess(asList(industrialFinances, academicFinances)));
        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(project));
        when(projectRestService.getLeadOrganisationByProject(projectId)).thenReturn(restSuccess(newOrganisationResource().withId(1L).build()));
        when(projectFinanceRestService.hasAnyProjectOrganisationSizeChangedFromApplication(projectId)).thenReturn(restSuccess(false));
        when(competitionRestService.getCompetitionById(project.getCompetition())).thenReturn(restSuccess(competition));
        when(applicationFinanceRestService.getFinanceTotals(project.getApplication())).thenReturn(restSuccess(asList(applicationIndustrialFinances, applicationAcademicFinances)));

        MvcResult result = mockMvc.perform(get("/project/{projectId}/funding-level", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/financecheck/funding-level"))
                .andReturn();

        ProjectFinanceFundingLevelViewModel viewModel = (ProjectFinanceFundingLevelViewModel) result.getModelAndView().getModel().get("model");

        BigDecimal totalGrant = industrialFinances.getTotalFundingSought().add(academicFinances.getTotalFundingSought());
        assertEquals("Project", viewModel.getProjectName());
        assertEquals(5L, viewModel.getApplicationId());
        assertEquals(projectId, viewModel.getProjectId());
        assertEquals(totalGrant, viewModel.getTotalFundingSought());
        assertEquals(industrialFinances.getTotal().add(academicFinances.getTotal()), viewModel.getTotalCosts());
        assertEquals(2, viewModel.getPartners().size());

        ProjectFinancePartnerFundingLevelViewModel industrialViewModel = viewModel.getPartners().get(0);
        assertEquals(industrialOrganisation, industrialViewModel.getId());
        assertEquals(60, industrialViewModel.getMaximumFundingLevel());
        assertEquals(industrialFinances.getTotal(), industrialViewModel.getCosts());
        assertEquals(industrialFinances.getTotalFundingSought(), industrialViewModel.getFundingSought());
        assertEquals(new BigDecimal("85.86"), industrialViewModel.getPercentageOfTotalGrant().setScale(MAX_DECIMAL_PLACES, RoundingMode.HALF_UP));
        assertEquals(BigDecimal.ZERO, industrialViewModel.getOtherFunding());
        assertEquals(totalGrant, industrialViewModel.getTotalGrant());

        ProjectFinancePartnerFundingLevelViewModel academicViewModel = viewModel.getPartners().get(1);
        assertEquals(academicOrganisation, academicViewModel.getId());
        assertEquals(100, academicViewModel.getMaximumFundingLevel());
        assertEquals(academicFinances.getTotal(), academicViewModel.getCosts());
        assertEquals(academicFinances.getTotalFundingSought(), academicViewModel.getFundingSought());
        assertEquals(new BigDecimal("14.14"), academicViewModel.getPercentageOfTotalGrant().setScale(MAX_DECIMAL_PLACES, RoundingMode.HALF_UP));
        assertEquals(BigDecimal.ZERO, academicViewModel.getOtherFunding());
        assertEquals(totalGrant, academicViewModel.getTotalGrant());
    }

    @Test
    public void saveFundingLevels_success() throws Exception {
        when(projectFinanceRestService.getProjectFinances(projectId)).thenReturn(restSuccess(asList(industrialFinances, academicFinances)));
        when(financeRowRestService.update(any())).thenReturn(restSuccess(ValidationMessages.noErrors()));

        mockMvc.perform(post("/project/{projectId}/funding-level", projectId)
                .param(format("partners[%d].fundingLevel", industrialOrganisation), "60")
                .param(format("partners[%d].fundingLevel", academicOrganisation), "60"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/project/%d/finance-check-overview", projectId)))
                .andReturn();

        verify(financeRowRestService).update(academicFinances.getGrantClaim());
        verify(financeRowRestService).update(industrialFinances.getGrantClaim());

        assertEquals(BigDecimal.valueOf(60), academicFinances.getGrantClaimPercentage());
        assertEquals(BigDecimal.valueOf(60), industrialFinances.getGrantClaimPercentage());
    }

    @Test
    public void saveFundingLevels_invalid() throws Exception {
        when(projectFinanceRestService.getProjectFinances(projectId)).thenReturn(restSuccess(asList(industrialFinances, academicFinances)));
        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(project));
        when(projectRestService.getLeadOrganisationByProject(projectId)).thenReturn(restSuccess(newOrganisationResource().withId(1L).build()));
        when(competitionRestService.getCompetitionById(project.getCompetition())).thenReturn(restSuccess(competition));
        when(applicationFinanceRestService.getFinanceTotals(project.getApplication())).thenReturn(restSuccess(asList(applicationIndustrialFinances, applicationAcademicFinances)));

        mockMvc.perform(post("/project/{projectId}/funding-level", projectId)
                .param(format("partners[%d].fundingLevel", industrialOrganisation), "100")
                .param(format("partners[%d].fundingLevel", academicOrganisation), "100"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("project/financecheck/funding-level"))
                .andExpect(model().attributeHasFieldErrorCode("form", format("partners[%d].fundingLevel", industrialOrganisation),"validation.finance.grant.claim.percentage.max"))
                .andReturn();

        verifyZeroInteractions(financeRowRestService);
    }

    @Test
    public void saveFundingLevels_invalidZeroFundingLevel() throws Exception {
        when(projectFinanceRestService.getProjectFinances(projectId)).thenReturn(restSuccess(asList(industrialFinances, academicFinances)));
        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(project));
        when(projectRestService.getLeadOrganisationByProject(projectId)).thenReturn(restSuccess(newOrganisationResource().withId(1L).build()));
        when(competitionRestService.getCompetitionById(project.getCompetition())).thenReturn(restSuccess(competition));
        when(applicationFinanceRestService.getFinanceTotals(project.getApplication())).thenReturn(restSuccess(asList(applicationIndustrialFinances, applicationAcademicFinances)));

        mockMvc.perform(post("/project/{projectId}/funding-level", projectId)
                .param(format("partners[%d].fundingLevel", industrialOrganisation), "0")
                .param(format("partners[%d].fundingLevel", academicOrganisation), "0"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("project/financecheck/funding-level"))
                .andExpect(model().attributeHasFieldErrorCode("form", format("partners[%d].fundingLevel", industrialOrganisation),"DecimalMin"))
                .andReturn();

        verifyZeroInteractions(financeRowRestService);
    }

    @Test
    public void viewFundingLevels_withChangeInOrganisationSize() throws Exception {
        industrialFinances.setOrganisationSize(OrganisationSize.LARGE);
        when(projectFinanceRestService.getProjectFinances(projectId)).thenReturn(restSuccess(asList(industrialFinances, academicFinances)));
        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(project));
        when(projectRestService.getLeadOrganisationByProject(projectId)).thenReturn(restSuccess(newOrganisationResource().withId(1L).build()));
        when(projectFinanceRestService.hasAnyProjectOrganisationSizeChangedFromApplication(projectId)).thenReturn(restSuccess(true));
        when(competitionRestService.getCompetitionById(project.getCompetition())).thenReturn(restSuccess(competition));
        when(applicationFinanceRestService.getFinanceTotals(project.getApplication())).thenReturn(restSuccess(asList(applicationIndustrialFinances, applicationAcademicFinances)));

        MvcResult result = mockMvc.perform(get("/project/{projectId}/funding-level", projectId))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("project/financecheck/funding-level"))
                .andReturn();

        ProjectFinanceFundingLevelViewModel viewModel = (ProjectFinanceFundingLevelViewModel) result.getModelAndView().getModel().get("model");

        BigDecimal totalGrant = industrialFinances.getTotalFundingSought().add(academicFinances.getTotalFundingSought());
        assertEquals("Project", viewModel.getProjectName());
        assertEquals(5L, viewModel.getApplicationId());
        assertEquals(projectId, viewModel.getProjectId());
        assertEquals(totalGrant, viewModel.getTotalFundingSought());
        assertEquals(industrialFinances.getTotal().add(academicFinances.getTotal()), viewModel.getTotalCosts());
        assertEquals(2, viewModel.getPartners().size());

        ProjectFinancePartnerFundingLevelViewModel industrialViewModel = viewModel.getPartners().get(0);
        assertEquals(industrialOrganisation, industrialViewModel.getId());
        assertEquals(60, industrialViewModel.getMaximumFundingLevel());
        assertEquals(industrialFinances.getTotal(), industrialViewModel.getCosts());
        assertEquals(industrialFinances.getTotalFundingSought(), industrialViewModel.getFundingSought());
        assertEquals(new BigDecimal("85.86"), industrialViewModel.getPercentageOfTotalGrant().setScale(2, RoundingMode.HALF_UP));
        assertEquals(BigDecimal.ZERO, industrialViewModel.getOtherFunding());
        assertEquals(totalGrant, industrialViewModel.getTotalGrant());

        ProjectFinancePartnerFundingLevelViewModel academicViewModel = viewModel.getPartners().get(1);
        assertEquals(academicOrganisation, academicViewModel.getId());
        assertEquals(100, academicViewModel.getMaximumFundingLevel());
        assertEquals(academicFinances.getTotal(), academicViewModel.getCosts());
        assertEquals(academicFinances.getTotalFundingSought(), academicViewModel.getFundingSought());
        assertEquals(new BigDecimal("14.14"), academicViewModel.getPercentageOfTotalGrant().setScale(2, RoundingMode.HALF_UP));
        assertEquals(BigDecimal.ZERO, academicViewModel.getOtherFunding());
        assertEquals(totalGrant, academicViewModel.getTotalGrant());
    }
}
