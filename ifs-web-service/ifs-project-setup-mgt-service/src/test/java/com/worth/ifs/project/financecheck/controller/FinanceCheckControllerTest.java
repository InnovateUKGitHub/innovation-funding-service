package com.worth.ifs.project.financecheck.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.finance.builder.ApplicationFinanceResourceBuilder;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.project.builder.SpendProfileResourceBuilder;
import com.worth.ifs.project.finance.resource.FinanceCheckResource;
import com.worth.ifs.project.financecheck.form.FinanceCheckForm;
import com.worth.ifs.project.financecheck.viewmodel.FinanceCheckViewModel;
import com.worth.ifs.project.financecheck.viewmodel.ProjectFinanceCheckSummaryViewModel;
import com.worth.ifs.project.resource.ProjectOrganisationCompositeId;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.SpendProfileResource;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.OrganisationTypeEnum;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.application.builder.CompetitionSummaryResourceBuilder.newCompetitionSummaryResource;
import static com.worth.ifs.project.builder.FinanceCheckResourceBuilder.newFinanceCheckResource;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.project.finance.builder.FinanceCheckProcessResourceBuilder.newFinanceCheckProcessResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.util.CollectionFunctions.mapWithIndex;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FinanceCheckControllerTest extends BaseControllerMockMVCTest<FinanceCheckController> {
    @Test
    public void testView() throws Exception {
        Long competitionId = 1L;
        Long projectId = 123L;
        Long organisationId = 456L;
        Long applicationId = 789L;

        ProjectOrganisationCompositeId key = new ProjectOrganisationCompositeId(projectId, organisationId);
        OrganisationResource organisationResource = newOrganisationResource().withId(organisationId).withOrganisationType(OrganisationTypeEnum.BUSINESS.getOrganisationTypeId()).build();
        ApplicationResource applicationResource = newApplicationResource().withId(applicationId).build();
        ProjectResource projectResource = newProjectResource().withId(projectId).withApplication(applicationResource).build();
        when(applicationService.getById(applicationId)).thenReturn(newApplicationResource().withId(applicationId).withCompetition(competitionId).build());
        when(projectService.getById(projectId)).thenReturn(projectResource);

        when(organisationService.getOrganisationById(organisationId)).thenReturn(organisationResource);
        when(financeCheckServiceMock.getByProjectAndOrganisation(key)).thenReturn(newFinanceCheckResource().build());
        when(projectFinanceService.getFinanceCheckApprovalStatus(projectId, organisationId)).thenReturn(
                newFinanceCheckProcessResource().
                        withCanApprove(true).
                        withInternalParticipant(newUserResource().withFirstName("Mr").withLastName("Approver").build()).
                        withModifiedDate(LocalDateTime.of(2016, 10, 04, 12, 13, 14)).
                        build());

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + organisationId)).
                andExpect(view().name("project/financecheck/partner-project-eligibility")).
                andReturn();

        FinanceCheckViewModel financeCheckViewModel = (FinanceCheckViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(LocalDate.of(2016, 10, 04), financeCheckViewModel.getApprovalDate());
        assertEquals("Mr Approver", financeCheckViewModel.getApproverName());
        assertNull(financeCheckViewModel.getFinanceContactEmail());
        assertNull(financeCheckViewModel.getFinanceContactName());
        assertTrue(financeCheckViewModel.isFinanceChecksApproved());
        assertFalse(financeCheckViewModel.isResearch());

        FinanceCheckForm form = (FinanceCheckForm) result.getModelAndView().getModel().get("form");
        assertEquals(form.getCosts().size(), 0);
    }

    @Test
    public void testUpdate() throws Exception {
        Long projectId = 123L;
        Long organisationId = 456L;
        FinanceCheckResource financeCheckResource = newFinanceCheckResource().build();
        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + organisationId).
                contentType(MediaType.APPLICATION_FORM_URLENCODED)).
                andExpect(status().is3xxRedirection()).
                andReturn();
    }

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
        List<ProjectFinanceCheckSummaryViewModel.FinanceCheckOrganisationRow> expectedOrganisationRows = mapWithIndex(organisationResourceList, (i, org) ->

                new ProjectFinanceCheckSummaryViewModel.FinanceCheckOrganisationRow(
                        org.getId(), org.getName(),
                        getEnumForIndex(ProjectFinanceCheckSummaryViewModel.Viability.class, i),
                        getEnumForIndex(ProjectFinanceCheckSummaryViewModel.RagStatus.class, i),
                        getEnumForIndex(ProjectFinanceCheckSummaryViewModel.Eligibility.class, i),
                        getEnumForIndex(ProjectFinanceCheckSummaryViewModel.RagStatus.class, i + 1),
                        getEnumForIndex(ProjectFinanceCheckSummaryViewModel.QueriesRaised.class, i))
        );

        ProjectFinanceCheckSummaryViewModel expectedProjectSpendProfileSummaryViewModel =  new ProjectFinanceCheckSummaryViewModel(
                projectResource.getId(), competitionSummaryResource, expectedOrganisationRows,
                projectResource.getTargetStartDate(), projectResource.getDurationInMonths().intValue(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                anySpendProfile.isPresent());

        mockMvc.perform(get("/project/{projectId}/finance-check", projectResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedProjectSpendProfileSummaryViewModel))
                .andExpect(view().name("project/financecheck/summary"))
        ;

    }

    private <T extends Enum> T getEnumForIndex(Class<T> enums, int index) {
        T[] enumConstants = enums.getEnumConstants();
        return enumConstants[index % enumConstants.length];
    }

    @Override
    protected FinanceCheckController supplyControllerUnderTest() {
        return new FinanceCheckController();
    }
}
