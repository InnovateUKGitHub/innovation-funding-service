package com.worth.ifs.finance.controller;

import com.worth.ifs.BaseControllerTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.finance.controller.ApplicationFinanceController;
import com.worth.ifs.finance.repository.ApplicationFinanceRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationFinanceControllerTest extends BaseControllerTest<ApplicationFinanceController> {


    ApplicationFinance applicationFinance;

    private MockMvc mockMvc;

    @Before
    public void setUp() {

        super.setUp();

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
