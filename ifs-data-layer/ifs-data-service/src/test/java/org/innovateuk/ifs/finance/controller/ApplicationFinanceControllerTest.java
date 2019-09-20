package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceRowService;
import org.innovateuk.ifs.finance.transactional.FinanceFileEntryService;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationFinanceControllerTest extends BaseControllerMockMVCTest<ApplicationFinanceController> {
    private ApplicationFinanceResource applicationFinanceResource;
    private Organisation organisation;
    private ApplicationResource application;

    @Mock
    private ApplicationFinanceService financeServiceMock;

    @Mock
    private FinanceFileEntryService financeFileEntryServiceMock;

    @Mock
    private ApplicationFinanceRowService financeRowCostsServiceMock;

    @Override
    protected ApplicationFinanceController supplyControllerUnderTest() {
        return new ApplicationFinanceController();
    }

    @Before
    public void setUp() {
        application = new ApplicationResource();
        application.setId(1L);
        organisation = new Organisation("Worth Internet Systems");
        applicationFinanceResource = newApplicationFinanceResource().withApplication(application.getId()).withOrganisation(organisation.getId()).build();
    }

    @Test
    public void applicationFinanceControllerShouldReturnApplicationByApplicationIdAndOrganisationId() throws Exception {

        when(financeServiceMock.findApplicationFinanceByApplicationIdAndOrganisation(123L, 456L)).thenReturn(serviceSuccess(applicationFinanceResource));

        mockMvc.perform(get("/applicationfinance/find-by-application-organisation/{applicationId}/{organisationId}", "123", "456"))
                .andExpect(status().isOk());

        verify(financeServiceMock, times(1)).findApplicationFinanceByApplicationIdAndOrganisation(123L, 456L);
    }

    @Test
    public void applicationFinanceControllerShouldReturnNotFoundOnMissingParams() throws Exception {

        mockMvc.perform(get("/applicationfinance/find-by-application-organisation/{applicationId}/", "1"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/applicationfinance/find-by-application-organisation/"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void applicationFinanceControllerShouldReturnBadRequestOnWrongParamType() throws Exception {

        mockMvc.perform(get("/applicationfinance/find-by-application-organisation/{applicationId}/{organisationId}", "1", "wrong"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/applicationfinance/find-by-application-organisation/{applicationId}/{organisationId}", "wrong", "1"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/applicationfinance/find-by-application-organisation/{applicationId}/{organisationId}", "wrong", "wrong"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void findByApplicationShouldReturnApplicationByApplicationId() throws Exception {

        when(financeServiceMock.findApplicationFinanceByApplication(123L)).thenReturn(serviceSuccess(singletonList(applicationFinanceResource)));

        mockMvc.perform(get("/applicationfinance/find-by-application/{applicationId}", "123"))
                .andExpect(status().isOk());

        verify(financeServiceMock, times(1)).findApplicationFinanceByApplication(123L);
    }

    @Test
    public void findByApplicationShouldReturnNotFoundOnMissingParams() throws Exception {

        mockMvc.perform(get("/applicationfinance/find-by-application/"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void findByApplicationShouldReturnBadRequestOnWrongParamType() throws Exception {

        mockMvc.perform(get("/applicationfinance/find-by-application/{applicationId}", "wrong"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addControllerShouldReturnNotFoundOnMissingParams() throws Exception {
        mockMvc.perform(post("/applicationfinance/add/{applicationId}/", "1"))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/applicationfinance/add/"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getFileDetails() throws Exception {

        FileEntryResource fileEntry = newFileEntryResource().build();

        when(financeFileEntryServiceMock.getFileContents(123)).thenReturn(serviceSuccess(new BasicFileAndContents(fileEntry, () -> null)));

        mockMvc.perform(get("/applicationfinance/finance-document/fileentry?applicationFinanceId=123"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(fileEntry)));
    }

    @Test
    public void financeDetails() throws Exception {
        ApplicationFinanceResource applicationFinanceResource = newApplicationFinanceResource().build();

        when(financeServiceMock.financeDetails(123L, 456L)).thenReturn(serviceSuccess(applicationFinanceResource));

        mockMvc.perform(get("/applicationfinance/finance-details/{applicationId}/{organisationId}", "123", "456"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(applicationFinanceResource)));

        verify(financeServiceMock).financeDetails(123L, 456L);
    }

    @Test
    public void getFinanceDetailsForApplication() throws Exception {
        List<ApplicationFinanceResource> applicationFinanceResources = newApplicationFinanceResource().build(3);

        when(financeServiceMock.financeDetails(123L)).thenReturn(serviceSuccess(applicationFinanceResources));

        mockMvc.perform(get("/applicationfinance/finance-details/{applicationId}", "123"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(applicationFinanceResources)));

        verify(financeServiceMock).financeDetails(123L);
    }
}
