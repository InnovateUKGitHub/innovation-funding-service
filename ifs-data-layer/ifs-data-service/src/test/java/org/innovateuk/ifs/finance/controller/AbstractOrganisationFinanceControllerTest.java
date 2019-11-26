package org.innovateuk.ifs.finance.controller;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithoutGrowthTableResource;
import org.innovateuk.ifs.finance.transactional.OrganisationFinanceService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

public class AbstractOrganisationFinanceControllerTest extends BaseControllerMockMVCTest<AbstractOrganisationFinanceController> {

    @Mock
    private OrganisationFinanceService organisationFinanceService;

    private static final long targetId = 1L;
    private static final long organisationId = 2L;

    @Override
    protected AbstractOrganisationFinanceController supplyControllerUnderTest() {
        return new extendsAbstractOrganisationFinanceController();
    }

    @RestController
    @RequestMapping("/root/{targetId}/organisation/{organisationId}/finance")
    class extendsAbstractOrganisationFinanceController extends AbstractOrganisationFinanceController {
        @Override
        protected OrganisationFinanceService getOrganisationFinanceService() {
            return organisationFinanceService;
        }
    }

    @Test
    public void getOrganisationWithGrowthTable() throws Exception {
        OrganisationFinancesWithGrowthTableResource expectedOrganisationFinances =
            new OrganisationFinancesWithGrowthTableResource();

        when(organisationFinanceService.
            getOrganisationWithGrowthTable(targetId, organisationId))
            .thenReturn(serviceSuccess(expectedOrganisationFinances));

        mockMvc.perform(get("/root/{targetId}/organisation/{organisationId}/finance/with-growth-table",
            targetId, organisationId))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(expectedOrganisationFinances)));
    }

    @Test
    public void getOrganisationWithoutGrowthTable() throws Exception {
        OrganisationFinancesWithoutGrowthTableResource expectedOrganisationFinances =
            new OrganisationFinancesWithoutGrowthTableResource();

        when(organisationFinanceService.getOrganisationWithoutGrowthTable(targetId, organisationId))
            .thenReturn(serviceSuccess(expectedOrganisationFinances));

        mockMvc.perform(get("/root/{targetId}/organisation/{organisationId}/finance/without-growth-table",
            targetId, organisationId))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(expectedOrganisationFinances)));
    }

    @Test
    public void updateOrganisationWithGrowthTable() throws Exception {
        OrganisationFinancesWithGrowthTableResource organisationFinances =
            new OrganisationFinancesWithGrowthTableResource();

        when(organisationFinanceService.updateOrganisationWithGrowthTable(targetId, organisationId,
            organisationFinances)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/root/{targetId}/organisation/{organisationId}/finance/with-growth-table",
            targetId, organisationId)
            .contentType(APPLICATION_JSON)
            .content(toJson(organisationFinances)))
            .andExpect(status().isOk());

        verify(organisationFinanceService, times(1)).updateOrganisationWithGrowthTable(targetId, organisationId,
            organisationFinances);
    }

    @Test
    public void updateOrganisationWithoutGrowthTable() throws Exception {
        OrganisationFinancesWithoutGrowthTableResource organisationFinances =
            new OrganisationFinancesWithoutGrowthTableResource();

        when(organisationFinanceService.updateOrganisationWithoutGrowthTable(targetId, organisationId,
            organisationFinances)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/root/{targetId}/organisation/{organisationId}/finance/without-growth-table",
            targetId, organisationId)
            .contentType(APPLICATION_JSON)
            .content(toJson(organisationFinances)))
            .andExpect(status().isOk());

        verify(organisationFinanceService, times(1)).updateOrganisationWithoutGrowthTable(targetId, organisationId,
            organisationFinances);
    }

    @Test
    public void isShowStateAidAgreement() throws Exception {
        when(organisationFinanceService.isShowStateAidAgreement(targetId, organisationId)).thenReturn(serviceSuccess(true));

        mockMvc.perform(get("/root/{targetId}/organisation/{organisationId}/finance/show-state-aid", targetId,
            organisationId)).andExpect(status().isOk())
            .andExpect(content().string("true"));
    }
}