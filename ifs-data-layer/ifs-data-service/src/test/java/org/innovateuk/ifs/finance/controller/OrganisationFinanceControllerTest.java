package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithoutGrowthTableResource;
import org.innovateuk.ifs.finance.transactional.OrganisationFinanceService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrganisationFinanceControllerTest extends BaseControllerMockMVCTest<OrganisationFinanceController> {

    @Mock
    private OrganisationFinanceService organisationFinanceService;

    @Override
    protected OrganisationFinanceController supplyControllerUnderTest() {
        return new OrganisationFinanceController();
    }

    @Test
    public void getOrganisationWithGrowthTable() throws Exception {
        OrganisationFinancesWithGrowthTableResource expectedOrganisationFinances = new OrganisationFinancesWithGrowthTableResource();
        long applicationId = 1L;
        long organisationId = 2L;

        when(organisationFinanceService.getOrganisationWithGrowthTable(applicationId, organisationId)).thenReturn(serviceSuccess(expectedOrganisationFinances));

        mockMvc.perform(get("/application/{applicationId}/organisation/{organisationId}/finance/with-growth-table", applicationId, organisationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedOrganisationFinances)));
    }

    @Test
    public void getOrganisationWithoutGrowthTable() throws Exception {
        OrganisationFinancesWithoutGrowthTableResource expectedOrganisationFinances = new OrganisationFinancesWithoutGrowthTableResource();
        long applicationId = 1L;
        long organisationId = 2L;

        when(organisationFinanceService.getOrganisationWithoutGrowthTable(applicationId, organisationId)).thenReturn(serviceSuccess(expectedOrganisationFinances));

        mockMvc.perform(get("/application/{applicationId}/organisation/{organisationId}/finance/without-growth-table", applicationId, organisationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedOrganisationFinances)));
    }

    @Test
    public void updateOrganisationWithGrowthTable() throws Exception {
        OrganisationFinancesWithGrowthTableResource finances = new OrganisationFinancesWithGrowthTableResource();
        long applicationId = 1L;
        long organisationId = 2L;

        when(organisationFinanceService.updateOrganisationWithGrowthTable(applicationId, organisationId, finances)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/application/{applicationId}/organisation/{organisationId}/finance/with-growth-table", applicationId, organisationId)
                .contentType(APPLICATION_JSON)
                .content(toJson(finances)))
                .andExpect(status().isOk());

    }

    @Test
    public void updateOrganisationWithoutGrowthTable() throws Exception {
        OrganisationFinancesWithoutGrowthTableResource finances = new OrganisationFinancesWithoutGrowthTableResource();
        long applicationId = 1L;
        long organisationId = 2L;

        when(organisationFinanceService.updateOrganisationWithoutGrowthTable(applicationId, organisationId, finances)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/application/{applicationId}/organisation/{organisationId}/finance/without-growth-table", applicationId, organisationId)
                .contentType(APPLICATION_JSON)
                .content(toJson(finances)))
                .andExpect(status().isOk());
    }

    @Test
    public void isShowStateAidAgreement() throws Exception {
        long applicationId = 1L;
        long organisationId = 2L;

        when(organisationFinanceService.isShowStateAidAgreement(applicationId, organisationId)).thenReturn(serviceSuccess(true));

        mockMvc.perform(get("/application/{applicationId}/organisation/{organisationId}/finance/show-state-aid", applicationId, organisationId))
                .andExpect(status().isOk());
    }
}