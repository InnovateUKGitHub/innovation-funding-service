package com.worth.ifs.project.financecheck.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.file.controller.viewmodel.FileDetailsViewModel;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.resource.builders.FileEntryResourceBuilder;
import com.worth.ifs.finance.builder.ApplicationFinanceResourceBuilder;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.project.finance.resource.FinanceCheckResource;
import com.worth.ifs.project.finance.resource.FinanceCheckState;
import com.worth.ifs.project.financecheck.form.FinanceCheckForm;
import com.worth.ifs.project.financecheck.viewmodel.FinanceCheckViewModel;
import com.worth.ifs.project.financecheck.viewmodel.ProjectFinanceCheckSummaryViewModel;
import com.worth.ifs.project.resource.*;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.OrganisationTypeEnum;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.application.builder.CompetitionSummaryResourceBuilder.newCompetitionSummaryResource;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.project.builder.CostGroupResourceBuilder.newCostGroupResource;
import static com.worth.ifs.project.builder.FinanceCheckResourceBuilder.newFinanceCheckResource;
import static com.worth.ifs.project.builder.PartnerOrganisationResourceBuilder.newPartnerOrganisationResource;
import static com.worth.ifs.project.builder.ProjectLeadStatusResourceBuilder.newProjectLeadStatusResource;
import static com.worth.ifs.project.builder.ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static com.worth.ifs.project.builder.SpendProfileResourceBuilder.newSpendProfileResource;
import static com.worth.ifs.project.constant.ProjectActivityStates.*;
import static com.worth.ifs.project.finance.builder.FinanceCheckProcessResourceBuilder.newFinanceCheckProcessResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.util.CollectionFunctions.mapWithIndex;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
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
        Long financeFileEntry = 1L;

        ProjectOrganisationCompositeId key = new ProjectOrganisationCompositeId(projectId, organisationId);
        OrganisationResource organisationResource = newOrganisationResource().withId(organisationId).withOrganisationType(OrganisationTypeEnum.BUSINESS.getOrganisationTypeId()).build();
        ApplicationResource applicationResource = newApplicationResource().withId(applicationId).build();
        ProjectResource projectResource = newProjectResource().withId(projectId).withApplication(applicationResource).build();
        List<PartnerOrganisationResource> partnerOrganisationResources = newPartnerOrganisationResource().withProject(projectId).build(3);
        ApplicationFinanceResource applicationFinanceResource = ApplicationFinanceResourceBuilder.newApplicationFinanceResource()
                .withFinanceFileEntry(financeFileEntry)
                .build();
        FileEntryResource jesFileEntryResource = FileEntryResourceBuilder.newFileEntryResource().build();

        when(applicationService.getById(applicationId)).thenReturn(newApplicationResource().withId(applicationId).withCompetition(competitionId).build());
        when(projectService.getById(projectId)).thenReturn(projectResource);

        when(organisationService.getOrganisationById(organisationId)).thenReturn(organisationResource);
        when(financeCheckServiceMock.getByProjectAndOrganisation(key)).thenReturn(newFinanceCheckResource().build());
        when(financeCheckServiceMock.getFinanceCheckApprovalStatus(projectId, organisationId)).thenReturn(
                newFinanceCheckProcessResource().
                        withCanApprove(true).
                        withState(FinanceCheckState.APPROVED).
                        withInternalParticipant(newUserResource().withFirstName("Mr").withLastName("Approver").build()).
                        withModifiedDate(LocalDateTime.of(2016, 10, 04, 12, 13, 14)).
                        build());
        when(partnerOrganisationServiceMock.getPartnerOrganisations(projectId)).thenReturn(serviceSuccess(partnerOrganisationResources));

        when(financeService.getApplicationFinanceByApplicationIdAndOrganisationId(applicationResource.getId(), organisationId)).thenReturn(applicationFinanceResource);
        when(financeService.getFinanceEntry(applicationFinanceResource.getFinanceFileEntry())).thenReturn(RestResult.restSuccess(jesFileEntryResource));

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
        assertEquals(new FileDetailsViewModel(jesFileEntryResource), financeCheckViewModel.getJesFileDetails());

        FinanceCheckForm form = (FinanceCheckForm) result.getModelAndView().getModel().get("form");
        assertEquals(form.getCosts().size(), 0);
    }

    @Test
    public void testUpdate() throws Exception {
        Long projectId = 123L;
        Long organisationId = 456L;
        FinanceCheckResource financeCheckResource = newFinanceCheckResource().build();
        when(financeCheckServiceMock.getByProjectAndOrganisation(new ProjectOrganisationCompositeId(projectId, organisationId))).thenReturn(financeCheckResource);
        when(financeCheckServiceMock.update(financeCheckResource)).thenReturn(serviceSuccess());
        mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + organisationId).
                contentType(MediaType.APPLICATION_FORM_URLENCODED)).
                andExpect(status().is3xxRedirection());
    }

    @Test
    public void viewSpendProfileSummarySuccess() throws Exception {

        assertSpendProfileSummarySuccess(
                newProjectTeamStatusResource().
                        withProjectLeadStatus(newProjectLeadStatusResource().
                                withOrganisationId(999L).
                                withFinanceChecksStatus(COMPLETE).
                                build()).
                        withPartnerStatuses(newProjectPartnerStatusResource().
                                withFinanceChecksStatus(COMPLETE, NOT_REQUIRED).
                                build(2)).
                        build(), true);
    }

    @Test
    public void viewSpendProfileSummaryWhenFinanceChecksNotAllDone() throws Exception {

        assertSpendProfileSummarySuccess(
                newProjectTeamStatusResource().
                        withProjectLeadStatus(newProjectLeadStatusResource().
                                withFinanceChecksStatus(COMPLETE).
                                withOrganisationId(999L).
                                build()).
                        withPartnerStatuses(newProjectPartnerStatusResource().
                                withFinanceChecksStatus(ACTION_REQUIRED, NOT_REQUIRED).
                                build(2)).
                        build(), false);
    }

    private void assertSpendProfileSummarySuccess(ProjectTeamStatusResource projectTeamStatus, boolean expectedFinanceChecksApproved) throws Exception {

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

        OrganisationResource organisationResource = newOrganisationResource().withId(999L).build();
        List<OrganisationResource> organisationResourceList = new ArrayList<>();
        organisationResourceList.add(organisationResource);

        UserResource spendProfileGeneratedBy = newUserResource().build();
        Calendar spendProfileGeneratedDate = Calendar.getInstance();

        SpendProfileResource spendProfileResource = newSpendProfileResource().
                withGeneratedBy(spendProfileGeneratedBy).
                withGeneratedDate(spendProfileGeneratedDate).
                build();

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

        when(projectService.getProjectTeamStatus(projectResource.getId(), Optional.empty())).thenReturn(projectTeamStatus);

        // Expected Results
        List<ProjectFinanceCheckSummaryViewModel.FinanceCheckOrganisationRow> expectedOrganisationRows = mapWithIndex(organisationResourceList, (i, org) ->

                new ProjectFinanceCheckSummaryViewModel.FinanceCheckOrganisationRow(
                        org.getId(), org.getName(),
                        getEnumForIndex(ProjectFinanceCheckSummaryViewModel.Viability.class, i),
                        getEnumForIndex(ProjectFinanceCheckSummaryViewModel.RagStatus.class, i),
                        ProjectFinanceCheckSummaryViewModel.Eligibility.APPROVED,
                        getEnumForIndex(ProjectFinanceCheckSummaryViewModel.RagStatus.class, i + 1),
                        getEnumForIndex(ProjectFinanceCheckSummaryViewModel.QueriesRaised.class, i))
        );

        ProjectFinanceCheckSummaryViewModel expectedProjectSpendProfileSummaryViewModel = new ProjectFinanceCheckSummaryViewModel(
                projectResource.getId(), competitionSummaryResource, expectedOrganisationRows,
                projectResource.getTargetStartDate(), projectResource.getDurationInMonths().intValue(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                anySpendProfile.isPresent(),
                expectedFinanceChecksApproved, 
                spendProfileGeneratedBy.getName(),
                LocalDate.from(spendProfileGeneratedDate.toInstant().atOffset(ZoneOffset.UTC)));

        mockMvc.perform(get("/project/{projectId}/finance-check", projectResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedProjectSpendProfileSummaryViewModel))
                .andExpect(view().name("project/financecheck/summary"))
        ;
    }

    @Test
    public void testApproveFinanceCheck() throws Exception {

        FinanceCheckResource financeCheck = newFinanceCheckResource().
                withCostGroup(newCostGroupResource().build()).
                build();

        when(financeCheckServiceMock.update(financeCheck)).thenReturn(serviceSuccess());
        when(financeCheckServiceMock.approveFinanceCheck(123L, 456L)).thenReturn(serviceSuccess());
        when(financeCheckServiceMock.getByProjectAndOrganisation(new ProjectOrganisationCompositeId(123L, 456L))).thenReturn(financeCheck);

        mockMvc.perform(post("/project/{projectId}/finance-check/organisation/{organisationId}", 123L, 456L).
                param("approve", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/project/123/finance-check/organisation/456"));

        verify(financeCheckServiceMock).update(financeCheck);
        verify(financeCheckServiceMock).approveFinanceCheck(123L, 456L);
    }

    @Test
    public void testDownloadJesFileWhenFinanceFileEntryNotPresent() throws Exception {

        Long projectId = 1L;
        Long applicationId = 1L;
        Long organisationId = 1L;

        ProjectResource projectResource = newProjectResource()
                .withId(projectId)
                .withApplication(applicationId)
                .build();
        ApplicationResource applicationResource = newApplicationResource().withId(applicationId).build();

        ApplicationFinanceResource applicationFinanceResource = ApplicationFinanceResourceBuilder.newApplicationFinanceResource()
                .build();

        when(projectService.getById(projectId)).thenReturn(projectResource);
        when(applicationService.getById(projectResource.getApplication())).thenReturn(applicationResource);
        when(financeService.getApplicationFinanceByApplicationIdAndOrganisationId(applicationResource.getId(), organisationId)).thenReturn(applicationFinanceResource);

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + organisationId + "/jes-file"))
                .andExpect(status().isNoContent())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();

        // Assert that there is no content
        assertEquals("", response.getContentAsString());
        assertEquals(null, response.getHeader("Content-Disposition"));
        assertEquals(0, response.getContentLength());

    }

    @Test
    public void testDownloadJesFileSuccess() throws Exception {

        Long projectId = 1L;
        Long applicationId = 1L;
        Long organisationId = 1L;
        Long financeFileEntry = 1L;

        ProjectResource projectResource = newProjectResource()
                .withId(projectId)
                .withApplication(applicationId)
                .build();
        ApplicationResource applicationResource = newApplicationResource().withId(applicationId).build();

        ApplicationFinanceResource applicationFinanceResource = ApplicationFinanceResourceBuilder.newApplicationFinanceResource()
                .withFinanceFileEntry(financeFileEntry)
                .build();

        FileEntryResource jesFileEntryResource = FileEntryResourceBuilder.newFileEntryResource()
                .withName("jes-file.pdf")
                .build();
        byte[] content = "HelloWorld".getBytes();
        ByteArrayResource jesByteArrayResource = new ByteArrayResource(content);


        when(projectService.getById(projectId)).thenReturn(projectResource);
        when(applicationService.getById(projectResource.getApplication())).thenReturn(applicationResource);
        when(financeService.getApplicationFinanceByApplicationIdAndOrganisationId(applicationResource.getId(), organisationId)).thenReturn(applicationFinanceResource);
        when(financeService.getFinanceEntry(applicationFinanceResource.getFinanceFileEntry())).thenReturn(RestResult.restSuccess(jesFileEntryResource));
        when(financeService.getFinanceDocumentByApplicationFinance(applicationFinanceResource.getId())).thenReturn(RestResult.restSuccess(jesByteArrayResource));

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + organisationId + "/jes-file"))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        assertEquals("HelloWorld", response.getContentAsString());
        assertEquals("inline; filename=\"jes-file.pdf\"", response.getHeader("Content-Disposition"));
        assertEquals(10, response.getContentLength());

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
