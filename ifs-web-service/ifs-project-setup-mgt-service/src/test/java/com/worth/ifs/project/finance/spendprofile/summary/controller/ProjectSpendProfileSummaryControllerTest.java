package com.worth.ifs.project.finance.spendprofile.summary.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.finance.builder.ApplicationFinanceResourceBuilder;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.spendprofile.summary.controller.ProjectSpendProfileSummaryController;
import com.worth.ifs.finance.spendprofile.summary.viewmodel.ProjectSpendProfileSummaryViewModel;
import com.worth.ifs.project.builder.SpendProfileResourceBuilder;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.SpendProfileResource;
import com.worth.ifs.user.resource.OrganisationResource;
import org.junit.Test;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class ProjectSpendProfileSummaryControllerTest extends BaseControllerMockMVCTest<ProjectSpendProfileSummaryController> {

    @Test
    public void viewSpendProfileSummarySuccess() throws Exception {

        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().
                withId(123L).
                build();

        ApplicationResource applicationResource = newApplicationResource()
                .withCompetition(competitionSummaryResource.getCompetitionId())
                .build();

        ProjectResource projectResource = newProjectResource()
                .withApplication(applicationResource.getId())
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .withDuration(3L)
                .build();

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

        when(projectService.getById(projectResource.getId())).
                thenReturn(projectResource);

        when(applicationService.getById(applicationResource.getId())).
                thenReturn(applicationResource);

        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(competitionSummaryResource.getCompetitionId())).
                thenReturn(competitionSummaryResource);

        when(projectService.getPartnerOrganisationsForProject(projectResource.getId())).
                thenReturn(organisationResourceList);


        when(projectFinanceService.getSpendProfile(projectResource.getId(), organisationResource.getId())).
                thenReturn(anySpendProfile);

        when(financeService.getApplicationFinanceTotals(applicationResource.getId())).
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
                projectResource.getId(), competitionSummaryResource, expectedOrganisationRows,
                projectResource.getTargetStartDate(), projectResource.getDurationInMonths().intValue(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                anySpendProfile.isPresent());

        mockMvc.perform(get("/project/{projectId}/spend-profile/summary", projectResource.getId()))
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
