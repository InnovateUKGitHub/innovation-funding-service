package org.innovateuk.ifs.thread.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.project.finance.controller.ProjectFinanceQueriesController;
import org.innovateuk.threads.resource.QueryResource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.AlertDocs.alertResourceFields;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectFinanceQueriesControllerTest extends BaseControllerMockMVCTest<ProjectFinanceQueriesController> {

    @Autowired ProjectFinanceQueriesController projectFinanceQueriesController;

    @Override
    protected ProjectFinanceQueriesController supplyControllerUnderTest() {
        return projectFinanceQueriesController;
    }

    @Test
    public void testCreateQuery() throws Exception {
        QueryResource query = new QueryResource(2L, null, null, null, null, false, null);
        when(projectFinanceQueriesService.create(query)).thenReturn(serviceSuccess(2L));
        when(projectFinanceQueriesService.findAll(22L)).thenReturn(serviceSuccess(asList(query)));

        mockMvc.perform(get("/project/finance/queries/22"))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(asList(query))));


//        verify(projectFinanceQueriesService).create(query);
    }

//    @Test
//    public void getSpendProfileTable() throws Exception {
//
//        Long projectId = 1L;
//        Long organisationId = 1L;
//
//        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
//
//        SpendProfileTableResource expectedTable = new SpendProfileTableResource();
//
//        expectedTable.setMonths(asList(
//                new LocalDateResource(2016, 2, 1),
//                new LocalDateResource(2016, 3, 1),
//                new LocalDateResource(2016, 4, 1)
//        ));
//
//        expectedTable.setEligibleCostPerCategoryMap(asMap(
//                "Labour", new BigDecimal("100"),
//                "Materials", new BigDecimal("150"),
//                "Other costs", new BigDecimal("55")));
//
//        expectedTable.setMonthlyCostsPerCategoryMap(asMap(
//                "Labour", asList(new BigDecimal("30"), new BigDecimal("30"), new BigDecimal("40")),
//                "Materials", asList(new BigDecimal("70"), new BigDecimal("50"), new BigDecimal("60")),
//                "Other costs", asList(new BigDecimal("50"), new BigDecimal("5"), new BigDecimal("0"))));
//
//        when(projectFinanceServiceMock.getSpendProfileTable(projectOrganisationCompositeId)).thenReturn(serviceSuccess(expectedTable));
//
//        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile-table", projectId, organisationId))
//                .andExpect(status().isOk())
//                .andExpect(content().json(toJson(expectedTable)));
//    }
//
//
//    @Test
//    public void getSpendProfileCsv() throws Exception {
//
//        Long projectId = 1L;
//        Long organisationId = 1L;
//
//        ProjectResource projectResource = newProjectResource()
//                .withName("projectName1")
//                .withTargetStartDate(LocalDate.of(2018, 3, 1))
//                .withDuration(3L)
//                .withId(projectId)
//                .build();
//        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
//        SpendProfileTableResource expectedTable = buildSpendProfileTableResource(projectResource);
//
//        SpendProfileCSVResource expectedResource = new SpendProfileCSVResource();
//        expectedResource.setFileName("TEST_Spend_Profile_2016-10-30_10-11_12.csv");
//        expectedResource.setCsvData(generateTestCSVData(expectedTable));
//
//        when(projectFinanceServiceMock.getSpendProfileCSV(projectOrganisationCompositeId)).thenReturn(serviceSuccess(expectedResource));
//
//        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile-csv", projectId, organisationId))
//                .andExpect(status().isOk())
//                .andExpect(content().json(toJson(expectedResource)));
//    }
//
//    @Test
//    public void getSpendProfile() throws Exception {
//
//        Long projectId = 1L;
//        Long organisationId = 1L;
//
//        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
//
//        SpendProfileResource spendProfileResource = SpendProfileResourceBuilder.newSpendProfileResource().build();
//
//        when(projectFinanceServiceMock.getSpendProfile(projectOrganisationCompositeId)).thenReturn(serviceSuccess(spendProfileResource));
//
//        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectId, organisationId))
//                .andExpect(status().isOk())
//                .andExpect(content().json(toJson(spendProfileResource)));
//    }
//
//    @Test
//    public void saveSpendProfile() throws Exception {
//
//        Long projectId = 1L;
//        Long organisationId = 1L;
//
//        SpendProfileTableResource table = new SpendProfileTableResource();
//
//        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);
//
//        when(projectFinanceServiceMock.saveSpendProfile(projectOrganisationCompositeId, table)).thenReturn(serviceSuccess());
//
//        mockMvc.perform(post("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectId, organisationId)
//                .contentType(APPLICATION_JSON)
//                .content(toJson(table)))
//                .andExpect(status().isOk());
//    }

}
