package org.innovateuk.ifs.cofunder.dashboard.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.cofunder.dashboard.populator.CofunderDashboardModelPopulator;
import org.innovateuk.ifs.cofunder.dashboard.viewmodel.CofunderDashboardViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.Silent.class)
@TestPropertySource(locations = "classpath:application.properties")
public class CofunderDashboardControllerTest extends BaseControllerMockMVCTest<CofunderDashboardController> {

    @Override
    protected CofunderDashboardController supplyControllerUnderTest() {
        return new CofunderDashboardController();
    }

    @Mock
    private CofunderDashboardModelPopulator cofunderDashboardModelPopulator;

    @Test
    public void dashboard() throws Exception {
        CofunderDashboardViewModel expected = mock(CofunderDashboardViewModel.class);

        when(cofunderDashboardModelPopulator.populateModel(loggedInUser)).thenReturn(expected);

        MvcResult result = mockMvc.perform(get("/cofunder/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("cofunder-dashboard"))
                .andReturn();

        CofunderDashboardViewModel actual = (CofunderDashboardViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(expected, actual);
    }
}