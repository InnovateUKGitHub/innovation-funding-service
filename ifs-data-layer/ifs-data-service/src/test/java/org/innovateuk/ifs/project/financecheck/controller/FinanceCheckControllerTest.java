package org.innovateuk.ifs.project.financecheck.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.finance.resource.*;
import org.innovateuk.ifs.project.financechecks.controller.FinanceCheckController;
import org.innovateuk.ifs.project.financechecks.service.FinanceCheckService;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckPartnerStatusResourceBuilder.FinanceCheckEligibilityResourceBuilder.newFinanceCheckEligibilityResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckResourceBuilder.newFinanceCheckResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckSummaryResourceBuilder.newFinanceCheckSummaryResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FinanceCheckControllerTest extends BaseControllerMockMVCTest<FinanceCheckController> {

    @Mock
    private FinanceCheckService financeCheckServiceMock;

    @Test
    public void getFinanceCheck() throws Exception {
        long projectId = 123L;
        long organisationId = 456L;
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
        FinanceCheckResource expected = newFinanceCheckResource().build();
        when(financeCheckServiceMock.getByProjectAndOrganisation(projectOrganisationCompositeId)).thenReturn(serviceSuccess(expected));
        mockMvc.perform(get(FinanceCheckURIs.BASE_URL + "/{projectId}" + FinanceCheckURIs.ORGANISATION_PATH + "/{organisationId}" +  FinanceCheckURIs.PATH, projectId, organisationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));
        verify(financeCheckServiceMock).getByProjectAndOrganisation(projectOrganisationCompositeId);
    }

    @Test
    public void getFinanceCheckSummary() throws Exception {
        long projectId = 123L;
        FinanceCheckSummaryResource expected = newFinanceCheckSummaryResource().withPartnerStatusResources(emptyList()).build();
        when(financeCheckServiceMock.getFinanceCheckSummary(projectId)).thenReturn(serviceSuccess(expected));
        mockMvc.perform(get(FinanceCheckURIs.BASE_URL + "/{projectId}" + FinanceCheckURIs.PATH, projectId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));
        verify(financeCheckServiceMock).getFinanceCheckSummary(projectId);
    }

    @Test
    public void getFinanceCheckEligibility() throws Exception {
        long projectId = 123L;
        long organisationId = 234L;
        FinanceCheckEligibilityResource expected = newFinanceCheckEligibilityResource().build();
        when(financeCheckServiceMock.getFinanceCheckEligibilityDetails(projectId, organisationId)).thenReturn(serviceSuccess(expected));
        mockMvc.perform(get(FinanceCheckURIs.BASE_URL + "/{projectId}" + FinanceCheckURIs.ORGANISATION_PATH + "/{organisationId}" + FinanceCheckURIs.PATH + "/eligibility", projectId, organisationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));
        verify(financeCheckServiceMock).getFinanceCheckEligibilityDetails(projectId, organisationId);
    }

    @Test
    public void getFinanceCheckOverview() throws Exception {
        long projectId = 123L;
        when(financeCheckServiceMock.getFinanceCheckOverview(projectId)).thenReturn(serviceSuccess(new FinanceCheckOverviewResource()));

        mockMvc.perform(get(FinanceCheckURIs.BASE_URL + "/{projectId}" + FinanceCheckURIs.PATH + "/overview", projectId)).andExpect(status().isOk());

        verify(financeCheckServiceMock).getFinanceCheckOverview(projectId);
    }

    @Override
    protected FinanceCheckController supplyControllerUnderTest() {
        return new FinanceCheckController();
    }
}