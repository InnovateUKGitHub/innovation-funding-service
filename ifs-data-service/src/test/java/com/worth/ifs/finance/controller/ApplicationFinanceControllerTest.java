package com.worth.ifs.finance.controller;

import com.worth.ifs.BaseControllerMocksTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.user.domain.Organisation;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationFinanceControllerTest extends BaseControllerMocksTest<ApplicationFinanceController> {


    ApplicationFinance applicationFinance;

    @Override
    protected ApplicationFinanceController supplyControllerUnderTest() {
        return new ApplicationFinanceController();
    }

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
