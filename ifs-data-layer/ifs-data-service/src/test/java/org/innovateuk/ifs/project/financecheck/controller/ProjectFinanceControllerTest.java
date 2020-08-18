package org.innovateuk.ifs.project.financecheck.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceService;
import org.innovateuk.ifs.project.finance.resource.*;
import org.innovateuk.ifs.project.financechecks.controller.ProjectFinanceController;
import org.innovateuk.ifs.project.financechecks.service.FinanceCheckService;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.junit.Test;
import org.mockito.Mock;

import java.time.LocalDate;

import static freemarker.template.utility.Collections12.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckPartnerStatusResourceBuilder.newFinanceCheckPartnerStatusResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckSummaryResourceBuilder.newFinanceCheckSummaryResource;
import static org.innovateuk.ifs.project.finance.resource.EligibilityState.APPROVED;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectFinanceControllerTest extends BaseControllerMockMVCTest<ProjectFinanceController> {

    @Mock
    private FinanceCheckService financeCheckService;

    @Mock
    private ProjectFinanceService projectFinanceService;

    @Test
    public void getViability() throws Exception {
        Long projectId = 1L;
        Long organisationId = 2L;

        ViabilityResource expectedViabilityResource = new ViabilityResource(ViabilityState.APPROVED, ViabilityRagStatus.GREEN);
        expectedViabilityResource.setViabilityApprovalDate(LocalDate.now());
        expectedViabilityResource.setViabilityApprovalUserFirstName("Lee");
        expectedViabilityResource.setViabilityApprovalUserLastName("Bowman");

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        when(financeCheckService.getViability(projectOrganisationCompositeId)).thenReturn(serviceSuccess(expectedViabilityResource));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/viability", projectId, organisationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedViabilityResource)));
    }

    @Test
    public void saveViability() throws Exception {
        Long projectId = 1L;
        Long organisationId = 2L;
        ViabilityState viability = ViabilityState.APPROVED;
        ViabilityRagStatus viabilityRagStatus = ViabilityRagStatus.GREEN;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        when(financeCheckService.saveViability(projectOrganisationCompositeId, viability, viabilityRagStatus)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/viability/{viability}/{viabilityRagStatus}", projectId, organisationId, viability, viabilityRagStatus))
                .andExpect(status().isOk());
    }

    @Test
    public void getEligibility() throws Exception {
        Long projectId = 1L;
        Long organisationId = 2L;

        EligibilityResource expectedEligibilityResource = new EligibilityResource(APPROVED, EligibilityRagStatus.GREEN);
        expectedEligibilityResource.setEligibilityApprovalDate(LocalDate.now());
        expectedEligibilityResource.setEligibilityApprovalUserFirstName("Lee");
        expectedEligibilityResource.setEligibilityApprovalUserLastName("Bowman");

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        when(financeCheckService.getEligibility(projectOrganisationCompositeId)).thenReturn(serviceSuccess(expectedEligibilityResource));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/eligibility", projectId, organisationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedEligibilityResource)));
    }

    @Test
    public void saveEligibility() throws Exception {
        Long projectId = 1L;
        Long organisationId = 2L;

        EligibilityState eligibility = APPROVED;
        EligibilityRagStatus eligibilityRagStatus = EligibilityRagStatus.GREEN;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        when(financeCheckService.saveEligibility(projectOrganisationCompositeId, eligibility, eligibilityRagStatus)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/eligibility/{eligibility}/{eligibilityRagStatus}", projectId, organisationId, eligibility, eligibilityRagStatus))
                .andExpect(status().isOk());
    }

    @Test
    public void getCreditReport() throws Exception {
        Long projectId = 1L;
        Long organisationId = 2L;

        when(financeCheckService.getCreditReport(projectId, organisationId)).thenReturn(serviceSuccess(true));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/credit-report", projectId, organisationId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(financeCheckService).getCreditReport(projectId, organisationId);
    }

    @Test
    public void saveCreditReport() throws Exception {
        Long projectId = 1L;
        Long organisationId = 2L;

        when(financeCheckService.saveCreditReport(projectId, organisationId, true)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/credit-report/{viability}", projectId, organisationId, Boolean.TRUE))
                .andExpect(status().isOk());

        verify(financeCheckService).saveCreditReport(projectId, organisationId, true);
    }

    @Test
    public void financeDetails() throws Exception {
        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().build();

        when(projectFinanceService.financeChecksDetails(123L, 456L)).thenReturn(serviceSuccess(projectFinanceResource));

        mockMvc.perform(get("/project/{projectId}/organisation/{organisationId}/finance-details", "123", "456"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(projectFinanceResource)));

        verify(projectFinanceService).financeChecksDetails(123L, 456L);
    }

    @Test
    public void resetFinanceChecks() throws Exception {
        Long projectId = 1L;
        FinanceCheckPartnerStatusResource partnerStatusResource = newFinanceCheckPartnerStatusResource().withEligibility(APPROVED).withViability(ViabilityState.APPROVED).build();
        FinanceCheckSummaryResource finance = newFinanceCheckSummaryResource().withProjectId(projectId).withPartnerStatusResources(singletonList(partnerStatusResource)).build();

        when(financeCheckService.resetFinanceChecks(finance.getProjectId())).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/finance-checks/reset", projectId))
                .andExpect(status().isOk());

        verify(financeCheckService).resetFinanceChecks(projectId);
    }

    @Override
    protected ProjectFinanceController supplyControllerUnderTest() {
        return new ProjectFinanceController();
    }


}
