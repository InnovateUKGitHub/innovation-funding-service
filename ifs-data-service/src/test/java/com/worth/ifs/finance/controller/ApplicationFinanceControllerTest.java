package com.worth.ifs.finance.controller;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.repository.ApplicationFinanceRepository;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.repository.OrganisationRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationFinanceControllerTest {

    @Mock
    ApplicationFinanceRepository applicationFinanceRepository;

    @Mock
    OrganisationRepository organisationRepository;

    @Mock
    ApplicationRepository applicationRepository;

    private ApplicationFinance applicationFinance;

    private Organisation organisation;

    private Application application;

    private MockMvc mockMvc;

    @InjectMocks
    private ApplicationFinanceController applicationFinanceController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(applicationFinanceController)
                .build();

        application = new Application();
        application.setId(1L);
        organisation = new Organisation(1L, "Worth Internet Systems");
        applicationFinance = new ApplicationFinance(1L, application, organisation);
    }

    @Test
    public void applicationFinanceControllerShouldReturnApplicationByApplicationIdAndOrganisationId() throws Exception {
        when(applicationFinanceRepository.findByApplicationIdAndOrganisationId(anyLong(), anyLong())).thenReturn(applicationFinance);

        mockMvc.perform(get("/applicationfinance/findByApplicationOrganisation/{applicationId}/{organisationId}", "1", "1"))
                .andExpect(status().isOk());

        verify(applicationFinanceRepository, times(1)).findByApplicationIdAndOrganisationId(anyLong(),anyLong());
        verifyNoMoreInteractions(applicationFinanceRepository);
    }

    @Test
    public void applicationFinanceControllerShouldReturnNotFoundOnMissingParams() throws Exception {
        when(applicationFinanceRepository.findByApplicationIdAndOrganisationId(anyLong(), anyLong())).thenReturn(applicationFinance);

        mockMvc.perform(get("/applicationfinance/findByApplicationOrganisation/{applicationId}/", "1"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/applicationfinance/findByApplicationOrganisation/"))
                .andExpect(status().isNotFound());

        verifyNoMoreInteractions(applicationFinanceRepository);
    }

    @Test
    public void applicationFinanceControllerShouldReturnBadRequestOnWrongParamType() throws Exception {
        when(applicationFinanceRepository.findByApplicationIdAndOrganisationId(anyLong(), anyLong())).thenReturn(applicationFinance);

        mockMvc.perform(get("/applicationfinance/findByApplicationOrganisation/{applicationId}/{organisationId}", "1", "wrong"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/applicationfinance/findByApplicationOrganisation/{applicationId}/{organisationId}", "wrong", "1"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/applicationfinance/findByApplicationOrganisation/{applicationId}/{organisationId}", "wrong", "wrong"))
                .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(applicationFinanceRepository);
    }

    @Test
    public void findByApplicationShouldReturnApplicationByApplicationId() throws Exception {
        when(applicationFinanceRepository.findByApplicationId(anyLong())).thenReturn(Arrays.asList(applicationFinance));

        mockMvc.perform(get("/applicationfinance/findByApplication/{applicationId}", "1"))
                .andExpect(status().isOk());

        verify(applicationFinanceRepository, times(1)).findByApplicationId(anyLong());
        verifyNoMoreInteractions(applicationFinanceRepository);
    }

    @Test
    public void findByApplicationShouldReturnNotFoundOnMissingParams() throws Exception {
        when(applicationFinanceRepository.findByApplicationIdAndOrganisationId(anyLong(), anyLong())).thenReturn(applicationFinance);

        mockMvc.perform(get("/applicationfinance/findByApplication/"))
                .andExpect(status().isNotFound());

        verifyNoMoreInteractions(applicationFinanceRepository);
    }

    @Test
    public void findByApplicationShouldReturnBadRequestOnWrongParamType() throws Exception {
        when(applicationFinanceRepository.findByApplicationIdAndOrganisationId(anyLong(), anyLong())).thenReturn(applicationFinance);

        mockMvc.perform(get("/applicationfinance/findByApplication/{applicationId}", "wrong"))
                .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(applicationFinanceRepository);
    }

    @Test
    public void addShouldReturnApplicationByApplicationIdAndOrganisationId() throws Exception {
        when(applicationFinanceRepository.save(any(ApplicationFinance.class))).thenReturn(applicationFinance);
        when(applicationRepository.findOne(anyLong())).thenReturn(application);
        when(organisationRepository.findOne(anyLong())).thenReturn(organisation);

        mockMvc.perform(get("/applicationfinance/add/{applicationId}/{organisationId}", "1", "1"))
                .andExpect(status().isOk());

        verify(applicationRepository, times(1)).findOne(anyLong());
        verifyNoMoreInteractions(applicationRepository);
        verify(organisationRepository, times(1)).findOne(anyLong());
        verifyNoMoreInteractions(organisationRepository);
        verify(applicationFinanceRepository, times(1)).save(any(ApplicationFinance.class));
        verifyNoMoreInteractions(organisationRepository);
    }

    @Test
    public void addControllerShouldReturnNotFoundOnMissingParams() throws Exception {
        mockMvc.perform(get("/applicationfinance/add/{applicationId}/", "1"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/applicationfinance/add/"))
                .andExpect(status().isNotFound());

        verifyNoMoreInteractions(applicationFinanceRepository);
    }

    @Test
    public void addShouldReturnBadRequestOnWrongParamType() throws Exception {
        mockMvc.perform(get("/applicationfinance/add/{applicationId}/{organisationId}", "1", "wronger"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/applicationfinance/add/{applicationId}/{organisationId}", "wronger", "1"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/applicationfinance/add/{applicationId}/{organisationId}", "wronger", "wronger"))
                .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(applicationFinanceRepository);
    }

}
