package org.innovateuk.ifs.project.financechecks.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.finance.populator.ApplicationFundingBreakdownViewModelPopulator;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.financecheck.FinanceCheckService;
import org.innovateuk.ifs.financecheck.viewmodel.FinanceCheckOverviewViewModel;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckOverviewResource;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder.newPartnerOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckOverviewResourceBuilder.newFinanceCheckOverviewResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckPartnerStatusResourceBuilder.FinanceCheckEligibilityResourceBuilder.newFinanceCheckEligibilityResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckSummaryResourceBuilder.newFinanceCheckSummaryResource;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.class)
public class FinanceOverviewControllerTest extends BaseControllerMockMVCTest<FinanceOverviewController> {

    @Mock
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Mock
    private FinanceCheckService financeCheckServiceMock;

    @Mock
    private ProjectFinanceRestService projectFinanceService;

    @Mock
    private ProjectService projectService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private ApplicationFundingBreakdownViewModelPopulator applicationFundingBreakdownViewModelPopulator;

    @Test
    public void internalViews() throws Exception {
        long projectId = 123L;
        long organisationId = 456L;
        CompetitionResource competition = newCompetitionResource()
                .withFundingType(FundingType.LOAN)
                .withFinanceRowTypes(singletonList(FinanceRowType.GRANT_CLAIM_AMOUNT))
                .build();

        setLoggedInUser(admin);

        setExpectedMocks(projectId, organisationId, competition);
        MvcResult result = mockMvc.perform(get("/project/{projectId}/finance-check-overview", projectId))
                .andExpect(view().name("project/financecheck/overview"))
                .andReturn();
    
        FinanceCheckOverviewViewModel financeCheckOverviewViewModel = (FinanceCheckOverviewViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(LocalDate.of(2016, 1, 1), financeCheckOverviewViewModel.getOverview().getProjectStartDate());
        assertEquals("test-project", financeCheckOverviewViewModel.getOverview().getProjectName());
        assertFalse(financeCheckOverviewViewModel.isExternalUser());
        assertEquals("/project/123/finance-check", financeCheckOverviewViewModel.getExternalUserLinkUrl());

        verify(financeCheckServiceMock).getFinanceCheckOverview(projectId);
        verify(financeCheckServiceMock, times(3)).getFinanceCheckEligibilityDetails(anyLong(), isNull());
        verify(projectFinanceService).getProjectFinances(projectId);
    }

    private void setExpectedMocks(long projectId, long organisationId, CompetitionResource competition) {
        List<PartnerOrganisationResource> partnerOrganisationResources = newPartnerOrganisationResource()
                .withOrganisationName("EGGS", "Ludlow", "Empire").withLeadOrganisation(false, false, true).withProject(projectId).build(3);
        FinanceCheckEligibilityResource financeCheckEligibilityResource = newFinanceCheckEligibilityResource().withTotalCost(BigDecimal.valueOf(280009)).build();
        when(partnerOrganisationRestService.getProjectPartnerOrganisations(projectId)).thenReturn(restSuccess(partnerOrganisationResources));
        when(financeCheckServiceMock.getFinanceCheckOverview(projectId)).thenReturn(serviceSuccess(mockFinanceOverview()));
        when(projectFinanceService.getProjectFinances(projectId)).thenReturn(restSuccess(emptyList()));
        ProjectResource projectResource = newProjectResource().withCompetition(competition.getId()).build();
        when(projectService.getById(projectId)).thenReturn(projectResource);
        when(competitionRestService.getCompetitionById(projectResource.getCompetition())).thenReturn(restSuccess(competition));
        when(financeCheckServiceMock.getFinanceCheckSummary(projectId)).thenReturn(serviceSuccess(newFinanceCheckSummaryResource().withPartnerStatusResources(emptyList()).build()));
    }

    @Test
    public void externalViewsMo() throws Exception {
        long projectId = 123L;
        long organisationId = 456L;
        CompetitionResource competition = newCompetitionResource()
                .withFundingType(FundingType.LOAN)
                .withFinanceRowTypes(singletonList(FinanceRowType.GRANT_CLAIM_AMOUNT))
                .build();

        setLoggedInUser(monitoringOfficer);

        setExpectedMocks(projectId, organisationId, competition);
        MvcResult result = mockMvc.perform(get("/project/{projectId}/finance-check-overview", projectId))
                .andExpect(view().name("project/financecheck/overview"))
                .andReturn();

        assertExternalUserExpectation(result);

        verify(financeCheckServiceMock).getFinanceCheckOverview(projectId);
        verify(financeCheckServiceMock, times(3)).getFinanceCheckEligibilityDetails(anyLong(), isNull());
        verify(projectFinanceService).getProjectFinances(projectId);
    }

    private void assertExternalUserExpectation(MvcResult result) {
        FinanceCheckOverviewViewModel financeCheckOverviewViewModel = (FinanceCheckOverviewViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(LocalDate.of(2016, 1, 1), financeCheckOverviewViewModel.getOverview().getProjectStartDate());
        assertEquals("test-project", financeCheckOverviewViewModel.getOverview().getProjectName());
        assertTrue(financeCheckOverviewViewModel.isExternalUser());
        assertEquals("/project/123/finance-check/read-only", financeCheckOverviewViewModel.getExternalUserLinkUrl());
    }

    @Test
    public void externalViewsKta() throws Exception {
        long projectId = 123L;
        long organisationId = 456L;
        CompetitionResource competition = newCompetitionResource()
                .withFundingType(FundingType.LOAN)
                .withFinanceRowTypes(singletonList(FinanceRowType.GRANT_CLAIM_AMOUNT))
                .build();

        setLoggedInUser(kta);

        setExpectedMocks(projectId, organisationId, competition);
        MvcResult result = mockMvc.perform(get("/project/{projectId}/finance-check-overview", projectId))
                .andExpect(view().name("project/financecheck/overview"))
                .andReturn();

        assertExternalUserExpectation(result);

        verify(financeCheckServiceMock).getFinanceCheckOverview(projectId);
        verify(financeCheckServiceMock, times(3)).getFinanceCheckEligibilityDetails(anyLong(), isNull());
        verify(projectFinanceService).getProjectFinances(projectId);
    }

    @Override
    protected FinanceOverviewController supplyControllerUnderTest() {
        return new FinanceOverviewController();
    }

    private FinanceCheckOverviewResource mockFinanceOverview(){
        Long projectId = 123L;
        String projectName = "test-project";
        Integer durationInMonths = 22;
        BigDecimal grantApplied = BigDecimal.valueOf(650000);
        BigDecimal otherPublicSectorFunding = BigDecimal.valueOf(4500);
        BigDecimal totalPercentageGrant = BigDecimal.valueOf(22);
        BigDecimal totalProjectCost = BigDecimal.valueOf(750000);
        LocalDate startDate = LocalDate.of(2016, 01, 01);

        return newFinanceCheckOverviewResource().withProjectId().
                withProjectId(projectId).
                withProjectName(projectName).
                withGrantAppliedFor(grantApplied).
                withDurationInMonths(durationInMonths).
                withGrantAppliedFor(totalPercentageGrant).
                withOtherPublicSectorFunding(otherPublicSectorFunding).
                withProjectStartDate(startDate).
                withTotalProjectCost(totalProjectCost).build();
    }
}