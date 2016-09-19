package com.worth.ifs.project.finance.spendprofile.summary.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.finance.builder.ApplicationFinanceResourceBuilder;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.spendprofile.summary.controller.ProjectSpendProfileSummaryController;
import com.worth.ifs.finance.spendprofile.summary.viewmodel.ProjectSpendProfileSummaryViewModel;
import com.worth.ifs.project.builder.SpendProfileResourceBuilder;
import com.worth.ifs.project.resource.SpendProfileResource;
import com.worth.ifs.user.resource.OrganisationResource;
import org.junit.Test;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.project.resource.ProjectResource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.application.builder.CompetitionSummaryResourceBuilder.newCompetitionSummaryResource;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.util.CollectionFunctions.mapWithIndex;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


public class ProjectSpendProfileSummaryControllerTest extends BaseControllerMockMVCTest<ProjectSpendProfileSummaryController> {

    @Test
    public void viewSpendProfileSummarySuccess() throws Exception {

        Long projectId = 1L;

        ProjectResource projectResource = newProjectResource()
                .withApplication(1L)
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .withDuration(3L)
                .build();

        ApplicationResource applicationResource = newApplicationResource()
                .withCompetition(1L)
                .build();

        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().build();

        OrganisationResource organisationResource = newOrganisationResource().build();
        List<OrganisationResource> organisationResourceList = new ArrayList<>();
        organisationResourceList.add(organisationResource);

        SpendProfileResource spendProfileResource = SpendProfileResourceBuilder.newSpendProfileResource().build();

        Optional<SpendProfileResource> anySpendProfile = Optional.of(spendProfileResource);


        ApplicationFinanceResource applicationFinanceResource1 = ApplicationFinanceResourceBuilder.newApplicationFinanceResource()
                .withGrantClaimPercentage(20)
                .build();
        ApplicationFinanceResource applicationFinanceResource2 = ApplicationFinanceResourceBuilder.newApplicationFinanceResource()
                .withGrantClaimPercentage(20)
                .build();
        List<ApplicationFinanceResource> applicationFinanceResourceList = new ArrayList<>();
        applicationFinanceResourceList.add(applicationFinanceResource1);
        applicationFinanceResourceList.add(applicationFinanceResource2);

        when(projectService.getById(projectId)).
                thenReturn(projectResource);

        when(applicationService.getById(1L)).
                thenReturn(applicationResource);


        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(1L)).
                thenReturn(competitionSummaryResource);

        when(projectService.getPartnerOrganisationsForProject(projectId)).
                thenReturn(organisationResourceList);


        when(projectFinanceService.getSpendProfile(projectId, 1L)).
                thenReturn(anySpendProfile);

        when(financeService.getApplicationFinanceTotals(1L)).
                thenReturn(applicationFinanceResourceList);

        // Expected Results
        List<ProjectSpendProfileSummaryViewModel.SpendProfileOrganisationRow> expectedOrganisationRows = mapWithIndex(organisationResourceList, (i, org) ->

                new ProjectSpendProfileSummaryViewModel.SpendProfileOrganisationRow(
                        org.getId(), org.getName(),
                        getEnumForIndex(ProjectSpendProfileSummaryViewModel.Viability.class, i),
                        getEnumForIndex(ProjectSpendProfileSummaryViewModel.RagStatus.class, i),
                        getEnumForIndex(ProjectSpendProfileSummaryViewModel.Eligibility.class, i),
                        getEnumForIndex(ProjectSpendProfileSummaryViewModel.RagStatus.class, i + 1),
                        getEnumForIndex(ProjectSpendProfileSummaryViewModel.QueriesRaised.class, i))
        );

        ProjectSpendProfileSummaryViewModel expectedProjectSpendProfileSummaryViewModel =  new ProjectSpendProfileSummaryViewModel(
                projectId, competitionSummaryResource, expectedOrganisationRows,
                projectResource.getTargetStartDate(), projectResource.getDurationInMonths().intValue(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                anySpendProfile.isPresent());

        mockMvc.perform(get("/project/{projectId}/spend-profile/summary", projectId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedProjectSpendProfileSummaryViewModel))
                .andExpect(view().name("project/finance/spend-profile/summary"))
        ;

    }

    private <T extends Enum> T getEnumForIndex(Class<T> enums, int index) {
        T[] enumConstants = enums.getEnumConstants();
        return enumConstants[index % enumConstants.length];
    }

    @Override
    protected ProjectSpendProfileSummaryController supplyControllerUnderTest() {
        return new ProjectSpendProfileSummaryController();
    }
}
