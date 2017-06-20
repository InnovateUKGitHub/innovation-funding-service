package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResourceId;
import org.innovateuk.ifs.user.domain.Organisation;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

        mockMvc.perform(post("/applicationfinance/add/{applicationId}/{organisationId}", "123", "456"))
                .andExpect(status().isCreated());

        verify(financeRowServiceMock, times(1)).addCost(any(ApplicationFinanceResourceId.class));
    }

    @Test
    public void addControllerShouldReturnNotFoundOnMissingParams() throws Exception {
        mockMvc.perform(post("/applicationfinance/add/{applicationId}/", "1"))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/applicationfinance/add/"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void addShouldReturnBadRequestOnWrongParamType() throws Exception {
        mockMvc.perform(post("/applicationfinance/add/{applicationId}/{organisationId}", "1", "wronger"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/applicationfinance/add/{applicationId}/{organisationId}", "wronger", "1"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/applicationfinance/add/{applicationId}/{organisationId}", "wronger", "wronger"))
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

    @Test
    public void testFinanceDetails() throws Exception {
        ApplicationFinanceResource applicationFinanceResource = newApplicationFinanceResource().build();

        when(financeRowServiceMock.financeDetails(123L, 456L)).thenReturn(serviceSuccess(applicationFinanceResource));

        mockMvc.perform(get("/applicationfinance/financeDetails/{applicationId}/{organisationId}", "123", "456"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(applicationFinanceResource)));

        verify(financeRowServiceMock).financeDetails(123L, 456L);
    }

    @Test
    public void testGetFinanceDetailsForApplication() throws Exception {
        List<ApplicationFinanceResource> applicationFinanceResources = newApplicationFinanceResource().build(3);

        when(financeRowServiceMock.financeDetails(123L)).thenReturn(serviceSuccess(applicationFinanceResources));

        mockMvc.perform(get("/applicationfinance/financeDetails/{applicationId}", "123"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(applicationFinanceResources)));

        verify(financeRowServiceMock).financeDetails(123L);
    }
}
