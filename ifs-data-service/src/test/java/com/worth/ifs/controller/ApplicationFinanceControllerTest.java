package com.worth.ifs.controller;

import com.worth.ifs.domain.Application;
import com.worth.ifs.domain.ApplicationFinance;
import com.worth.ifs.domain.Organisation;
import com.worth.ifs.repository.ApplicationFinanceRepository;
import org.hibernate.annotations.SourceType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationFinanceControllerTest {

    @Mock
    ApplicationFinanceController applicationFinanceController;

    @Mock
    ApplicationFinanceRepository applicationFinanceRepository;

    ApplicationFinance applicationFinance;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(applicationFinanceController)
                .build();
        Application application = new Application();
        application.setId(1L);
        Organisation organisation = new Organisation(1L, "Worth Internet Systems");
        applicationFinance = new ApplicationFinance(1L, application, organisation);
    }

    @Test
    public void applicationFinanceControllerShouldReturnApplicationByApplicationIdAndOrganisationId() throws Exception {
        when(applicationFinanceRepository.findByApplicationIdAndOrganisationId(1L, 1L)).thenReturn(applicationFinance);
        mockMvc.perform(get("/applicationfinance/findByApplicationOrganisation/1/1"))
                .andExpect(status().isOk()
                );

    }
}
