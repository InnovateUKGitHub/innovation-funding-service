package org.innovateuk.ifs.project.financecheck.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.finance.resource.*;
import org.innovateuk.ifs.project.financechecks.controller.ProjectFinanceController;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectFinanceControllerTest extends BaseControllerMockMVCTest<ProjectFinanceController> {

    @Test
    public void testGetViability() throws Exception {
        Long projectId = 1L;
        Long organisationId = 2L;

        ViabilityResource expectedViabilityResource = new ViabilityResource(Viability.APPROVED, ViabilityRagStatus.GREEN);
        expectedViabilityResource.setViabilityApprovalDate(LocalDate.now());
        expectedViabilityResource.setViabilityApprovalUserFirstName("Lee");
        expectedViabilityResource.setViabilityApprovalUserLastName("Bowman");

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        when(financeCheckServiceMock.getViability(projectOrganisationCompositeId)).thenReturn(serviceSuccess(expectedViabilityResource));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/viability", projectId, organisationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedViabilityResource)));
    }

    @Test
    public void testSaveViability() throws Exception {
        Long projectId = 1L;
        Long organisationId = 2L;
        Viability viability = Viability.APPROVED;
        ViabilityRagStatus viabilityRagStatus = ViabilityRagStatus.GREEN;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        when(financeCheckServiceMock.saveViability(projectOrganisationCompositeId, viability, viabilityRagStatus)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/viability/{viability}/{viabilityRagStatus}", projectId, organisationId, viability, viabilityRagStatus))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetEligibility() throws Exception {
        Long projectId = 1L;
        Long organisationId = 2L;

        EligibilityResource expectedEligibilityResource = new EligibilityResource(Eligibility.APPROVED, EligibilityRagStatus.GREEN);
        expectedEligibilityResource.setEligibilityApprovalDate(LocalDate.now());
        expectedEligibilityResource.setEligibilityApprovalUserFirstName("Lee");
        expectedEligibilityResource.setEligibilityApprovalUserLastName("Bowman");

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        when(financeCheckServiceMock.getEligibility(projectOrganisationCompositeId)).thenReturn(serviceSuccess(expectedEligibilityResource));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/eligibility", projectId, organisationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedEligibilityResource)));
    }

    @Test
    public void testSaveEligibility() throws Exception {
        Long projectId = 1L;
        Long organisationId = 2L;

        Eligibility eligibility = Eligibility.APPROVED;
        EligibilityRagStatus eligibilityRagStatus = EligibilityRagStatus.GREEN;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        when(financeCheckServiceMock.saveEligibility(projectOrganisationCompositeId, eligibility, eligibilityRagStatus)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/eligibility/{eligibility}/{eligibilityRagStatus}", projectId, organisationId, eligibility, eligibilityRagStatus))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetCreditReport() throws Exception {
        Long projectId = 1L;
        Long organisationId = 2L;

        when(financeCheckServiceMock.getCreditReport(projectId, organisationId)).thenReturn(serviceSuccess(true));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/credit-report", projectId, organisationId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(financeCheckServiceMock).getCreditReport(projectId, organisationId);
    }

    @Test
    public void testSaveCreditReport() throws Exception {
        Long projectId = 1L;
        Long organisationId = 2L;

        when(financeCheckServiceMock.saveCreditReport(projectId, organisationId, true)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/credit-report/{viability}", projectId, organisationId, Boolean.TRUE))
                .andExpect(status().isOk());

        verify(financeCheckServiceMock).saveCreditReport(projectId, organisationId, true);
    }

    @Test
    public void testFinanceDetails() throws Exception {
        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().build();

        when(projectFinanceRowServiceMock.financeChecksDetails(123L, 456L)).thenReturn(serviceSuccess(projectFinanceResource));

        mockMvc.perform(MockMvcRequestBuilders.get("/project/{projectId}/organisation/{organisationId}/financeDetails", "123", "456"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(projectFinanceResource)));

        verify(projectFinanceRowServiceMock).financeChecksDetails(123L, 456L);
    }

    @Override
    protected ProjectFinanceController supplyControllerUnderTest() {
        return new ProjectFinanceController();
    }


}
