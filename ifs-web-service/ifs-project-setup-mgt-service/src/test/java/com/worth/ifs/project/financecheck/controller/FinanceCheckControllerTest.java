package com.worth.ifs.project.financecheck.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.project.finance.resource.FinanceCheckResource;
import com.worth.ifs.project.finance.resource.FinanceCheckState;
import com.worth.ifs.project.financecheck.form.FinanceCheckForm;
import com.worth.ifs.project.financecheck.viewmodel.FinanceCheckViewModel;
import com.worth.ifs.project.resource.PartnerOrganisationResource;
import com.worth.ifs.project.resource.ProjectOrganisationCompositeId;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.OrganisationTypeEnum;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.project.builder.CostGroupResourceBuilder.newCostGroupResource;
import static com.worth.ifs.project.builder.PartnerOrganisationResourceBuilder.newPartnerOrganisationResource;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.project.finance.builder.FinanceCheckProcessResourceBuilder.newFinanceCheckProcessResource;
import static com.worth.ifs.project.finance.builder.FinanceCheckResourceBuilder.newFinanceCheckResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
        List<PartnerOrganisationResource> partnerOrganisationResources = newPartnerOrganisationResource().withProject(projectId).build(3);

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
        when(financeCheckServiceMock.getByProjectAndOrganisation(new ProjectOrganisationCompositeId(projectId, organisationId))).thenReturn(financeCheckResource);
        when(financeCheckServiceMock.update(financeCheckResource)).thenReturn(serviceSuccess());
        mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + organisationId).
                contentType(MediaType.APPLICATION_FORM_URLENCODED)).
                andExpect(status().is3xxRedirection());
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

    @Override
    protected FinanceCheckController supplyControllerUnderTest() {
        return new FinanceCheckController();
    }
}
