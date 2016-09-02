package com.worth.ifs.project.finance.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.commons.rest.LocalDateResource;
import com.worth.ifs.project.builder.SpendProfileResourceBuilder;
import com.worth.ifs.project.controller.ProjectFinanceController;
import com.worth.ifs.project.resource.ProjectOrganisationCompositeId;
import com.worth.ifs.project.resource.SpendProfileResource;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import org.junit.Test;

import java.math.BigDecimal;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.JsonMappingUtil.toJson;
import static com.worth.ifs.util.MapFunctions.asMap;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectFinanceControllerTest extends BaseControllerMockMVCTest<ProjectFinanceController> {

    @Test
    public void testGenerateSpendProfile() throws Exception {

        when(projectFinanceServiceMock.generateSpendProfile(123L)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/123/spend-profile/generate")).
                andExpect(status().isCreated());

        verify(projectFinanceServiceMock).generateSpendProfile(123L);
    }

    @Test
    public void getSpendProfileTable() throws Exception {

        Long projectId = 1L;
        Long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        SpendProfileTableResource expectedTable = new SpendProfileTableResource();

        expectedTable.setMonths(asList(
                new LocalDateResource(2016, 2, 1),
                new LocalDateResource(2016, 3, 1),
                new LocalDateResource(2016, 4, 1)
        ));

        expectedTable.setEligibleCostPerCategoryMap(asMap(
                "Labour", new BigDecimal("100"),
                "Materials", new BigDecimal("150"),
                "Other costs", new BigDecimal("55")));

        expectedTable.setMonthlyCostsPerCategoryMap(asMap(
                "Labour", asList(new BigDecimal("30"), new BigDecimal("30"), new BigDecimal("40")),
                "Materials", asList(new BigDecimal("70"), new BigDecimal("50"), new BigDecimal("60")),
                "Other costs", asList(new BigDecimal("50"), new BigDecimal("5"), new BigDecimal("0"))));

        when(projectFinanceServiceMock.getSpendProfileTable(projectOrganisationCompositeId)).thenReturn(serviceSuccess(expectedTable));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile-table", projectId, organisationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedTable)));
    }

    @Test
    public void getSpendProfile() throws Exception {

        Long projectId = 1L;
        Long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        SpendProfileResource spendProfileResource = SpendProfileResourceBuilder.newSpendProfileResource().build();

        when(projectFinanceServiceMock.getSpendProfile(projectOrganisationCompositeId)).thenReturn(serviceSuccess(spendProfileResource));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectId, organisationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(spendProfileResource)));
    }

    @Test
    public void saveSpendProfile() throws Exception {

        Long projectId = 1L;
        Long organisationId = 1L;

        SpendProfileTableResource table = new SpendProfileTableResource();

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        when(projectFinanceServiceMock.saveSpendProfile(projectOrganisationCompositeId, table)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectId, organisationId)
                .contentType(APPLICATION_JSON)
                .content(toJson(table)))
                .andExpect(status().isOk());
    }

    @Test
    public void markSpendProfileCompete() throws Exception {

        Long projectId = 1L;
        Long organisationId = 1L;

        SpendProfileTableResource table = new SpendProfileTableResource();

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        when(projectFinanceServiceMock.markSpendProfileComplete(projectOrganisationCompositeId, table)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/spend-profile/complete", projectId, organisationId)
                .contentType(APPLICATION_JSON)
                .content(toJson(table)))
                .andExpect(status().isOk());
    }

    @Override
    protected ProjectFinanceController supplyControllerUnderTest() {
        return new ProjectFinanceController();
    }
}
