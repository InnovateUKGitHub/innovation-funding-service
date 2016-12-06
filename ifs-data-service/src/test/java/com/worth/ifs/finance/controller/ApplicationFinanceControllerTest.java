package com.worth.ifs.finance.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.service.BasicFileAndContents;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.ApplicationFinanceResourceId;
import com.worth.ifs.user.domain.Organisation;
import org.junit.Before;
import org.junit.Test;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static com.worth.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static com.worth.ifs.util.JsonMappingUtil.toJson;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationFinanceControllerTest extends BaseControllerMockMVCTest<ApplicationFinanceController> {
    private ApplicationFinanceResource applicationFinanceResource;
    private Organisation organisation;
    private ApplicationResource application;

    @Override
    protected ApplicationFinanceController supplyControllerUnderTest() {
        return new ApplicationFinanceController();
    }

    @Before
    public void setUp() {
        application = new ApplicationResource();
        application.setId(1L);
        organisation = new Organisation(1L, "Worth Internet Systems");
        applicationFinanceResource = newApplicationFinanceResource().withApplication(application.getId()).withOrganisation(organisation.getId()).build();
    }

    @Test
    public void applicationFinanceControllerShouldReturnApplicationByApplicationIdAndOrganisationId() throws Exception {

        when(financeRowServiceMock.findApplicationFinanceByApplicationIdAndOrganisation(123L, 456L)).thenReturn(serviceSuccess(applicationFinanceResource));

        mockMvc.perform(get("/applicationfinance/findByApplicationOrganisation/{applicationId}/{organisationId}", "123", "456"))
                .andExpect(status().isOk());

        verify(financeRowServiceMock, times(1)).findApplicationFinanceByApplicationIdAndOrganisation(123L, 456L);
    }

    @Test
    public void applicationFinanceControllerShouldReturnNotFoundOnMissingParams() throws Exception {

        mockMvc.perform(get("/applicationfinance/findByApplicationOrganisation/{applicationId}/", "1"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/applicationfinance/findByApplicationOrganisation/"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void applicationFinanceControllerShouldReturnBadRequestOnWrongParamType() throws Exception {

        mockMvc.perform(get("/applicationfinance/findByApplicationOrganisation/{applicationId}/{organisationId}", "1", "wrong"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/applicationfinance/findByApplicationOrganisation/{applicationId}/{organisationId}", "wrong", "1"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/applicationfinance/findByApplicationOrganisation/{applicationId}/{organisationId}", "wrong", "wrong"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void findByApplicationShouldReturnApplicationByApplicationId() throws Exception {

        when(financeRowServiceMock.findApplicationFinanceByApplication(123L)).thenReturn(serviceSuccess(singletonList(applicationFinanceResource)));

        mockMvc.perform(get("/applicationfinance/findByApplication/{applicationId}", "123"))
                .andExpect(status().isOk());

        verify(financeRowServiceMock, times(1)).findApplicationFinanceByApplication(123L);
    }

    @Test
    public void findByApplicationShouldReturnNotFoundOnMissingParams() throws Exception {

        mockMvc.perform(get("/applicationfinance/findByApplication/"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void findByApplicationShouldReturnBadRequestOnWrongParamType() throws Exception {

        mockMvc.perform(get("/applicationfinance/findByApplication/{applicationId}", "wrong"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addShouldReturnApplicationByApplicationIdAndOrganisationId() throws Exception {

        when(financeRowServiceMock.addCost(any(ApplicationFinanceResourceId.class))).thenReturn(serviceSuccess(applicationFinanceResource));

        mockMvc.perform(get("/applicationfinance/add/{applicationId}/{organisationId}", "123", "456"))
                .andExpect(status().isCreated());

        verify(financeRowServiceMock, times(1)).addCost(any(ApplicationFinanceResourceId.class));
    }

    @Test
    public void addControllerShouldReturnNotFoundOnMissingParams() throws Exception {
        mockMvc.perform(get("/applicationfinance/add/{applicationId}/", "1"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/applicationfinance/add/"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void addShouldReturnBadRequestOnWrongParamType() throws Exception {
        mockMvc.perform(get("/applicationfinance/add/{applicationId}/{organisationId}", "1", "wronger"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/applicationfinance/add/{applicationId}/{organisationId}", "wronger", "1"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/applicationfinance/add/{applicationId}/{organisationId}", "wronger", "wronger"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getFileDetails() throws Exception {

        FileEntryResource fileEntry = newFileEntryResource().build();

        when(financeRowServiceMock.getFileContents(123)).thenReturn(serviceSuccess(new BasicFileAndContents(fileEntry, () -> null)));

        mockMvc.perform(get("/applicationfinance/financeDocument/fileentry?applicationFinanceId=123"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(fileEntry)));
    }
}
