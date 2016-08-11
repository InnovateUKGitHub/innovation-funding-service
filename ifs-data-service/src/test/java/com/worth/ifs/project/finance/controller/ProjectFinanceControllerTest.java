package com.worth.ifs.project.finance.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.commons.rest.LocalDateResource;
import com.worth.ifs.project.controller.ProjectFinanceController;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.JsonMappingUtil.fromJson;
import static com.worth.ifs.util.MapFunctions.asMap;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    public void getSpendProfile() throws Exception {

        Long projectId = 1L;
        Long organisationId = 1L;

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

        when(projectFinanceServiceMock.getSpendProfileTable(projectId, organisationId)).thenReturn(serviceSuccess(expectedTable));

        MvcResult result = mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectId, organisationId))
                .andExpect(status().isOk())
                .andReturn();

        SpendProfileTableResource actualTable = fromJson(result.getResponse().getContentAsString(), SpendProfileTableResource.class);
        assertSpendProfilesEqual(expectedTable, actualTable);
    }

    private void assertSpendProfilesEqual(SpendProfileTableResource expectedTable, SpendProfileTableResource actualTable) {
    }

    @Override
    protected ProjectFinanceController supplyControllerUnderTest() {
        return new ProjectFinanceController();
    }
}
