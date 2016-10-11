package com.worth.ifs.project.finance.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.project.controller.FinanceCheckController;
import com.worth.ifs.project.finance.resource.FinanceCheckResource;
import com.worth.ifs.project.finance.resource.FinanceCheckSummaryResource;
import com.worth.ifs.project.finance.workflow.financechecks.resource.FinanceCheckProcessResource;
import com.worth.ifs.project.resource.ProjectOrganisationCompositeId;
import org.junit.Test;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.project.controller.FinanceCheckController.*;
import static com.worth.ifs.project.finance.builder.FinanceCheckProcessResourceBuilder.newFinanceCheckProcessResource;
import static com.worth.ifs.project.finance.builder.FinanceCheckResourceBuilder.newFinanceCheckResource;
import static com.worth.ifs.project.finance.builder.FinanceCheckSummaryResourceBuilder.newFinanceCheckSummaryResource;
import static com.worth.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FinanceCheckControllerTest extends BaseControllerMockMVCTest<FinanceCheckController> {

    @Test
    public void testGetFinanceCheck() throws Exception {
        Long projectId = 123L;
        Long organisationId = 456L;
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        FinanceCheckResource expected = newFinanceCheckResource().build();
        when(financeCheckServiceMock.getByProjectAndOrganisation(projectOrganisationCompositeId)).thenReturn(serviceSuccess(expected));
        mockMvc.perform(get(FINANCE_CHECK_BASE_URL + "/{projectId}" + FINANCE_CHECK_ORGANISATION_PATH + "/{organisationId}" +  FINANCE_CHECK_PATH, projectId, organisationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));
        verify(financeCheckServiceMock).getByProjectAndOrganisation(projectOrganisationCompositeId);
    }

    @Test
    public void testGetFinanceCheckSummary() throws Exception {
        Long projectId = 123L;
        FinanceCheckSummaryResource expected = newFinanceCheckSummaryResource().build();
        when(financeCheckServiceMock.getFinanceCheckSummary(projectId)).thenReturn(serviceSuccess(expected));
        mockMvc.perform(get(FINANCE_CHECK_BASE_URL + "/{projectId}" + FINANCE_CHECK_PATH, projectId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));
        verify(financeCheckServiceMock).getFinanceCheckSummary(projectId);
    }

    @Test
    public void testGetFinanceCheckApprovalStatus() throws Exception {
        Long projectId = 123L;
        Long organisationId = 456L;
        FinanceCheckProcessResource expected = newFinanceCheckProcessResource().build();
        when(financeCheckServiceMock.getFinanceCheckApprovalStatus(projectId, organisationId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get(FINANCE_CHECK_BASE_URL + "/{projectId}" + FINANCE_CHECK_ORGANISATION_PATH + "/{organisationId}" +  FINANCE_CHECK_PATH + "/status", projectId, organisationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(financeCheckServiceMock).getFinanceCheckApprovalStatus(projectId, organisationId);
    }

    @Test
    public void testApproveFinanceCheck() throws Exception {
        Long projectId = 123L;
        Long organisationId = 456L;
        when(financeCheckServiceMock.approve(projectId, organisationId)).thenReturn(serviceSuccess());

        mockMvc.perform(post(FINANCE_CHECK_BASE_URL + "/{projectId}" + FINANCE_CHECK_ORGANISATION_PATH + "/{organisationId}" +  FINANCE_CHECK_PATH + "/approve", projectId, organisationId))
                .andExpect(status().isOk());

        verify(financeCheckServiceMock).approve(123L, 456L);
    }

    @Test
    public void testUpdateFinanceCheck() throws Exception {
        FinanceCheckResource financeCheckResource = newFinanceCheckResource().build();
        when(financeCheckServiceMock.save(any(FinanceCheckResource.class))).thenReturn(serviceSuccess());
        mockMvc.perform(post(FINANCE_CHECK_BASE_URL + FINANCE_CHECK_PATH).
                contentType(APPLICATION_JSON).
                content(toJson(financeCheckResource))).
                andExpect(status().isOk());
    }

    @Override
    protected FinanceCheckController supplyControllerUnderTest() {
        return new FinanceCheckController();
    }
}