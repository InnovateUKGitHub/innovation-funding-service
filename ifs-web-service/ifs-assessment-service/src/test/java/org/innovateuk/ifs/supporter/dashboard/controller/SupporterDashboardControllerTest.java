package org.innovateuk.ifs.supporter.dashboard.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.supporter.dashboard.populator.SupporterDashboardModelPopulator;
import org.innovateuk.ifs.supporter.dashboard.viewmodel.SupporterDashboardViewModel;
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
public class SupporterDashboardControllerTest extends BaseControllerMockMVCTest<SupporterDashboardController> {

    @Override
    protected SupporterDashboardController supplyControllerUnderTest() {
        return new SupporterDashboardController();
    }

    @Mock
    private SupporterDashboardModelPopulator supporterDashboardModelPopulator;

    @Test
    public void dashboard() throws Exception {
        SupporterDashboardViewModel expected = mock(SupporterDashboardViewModel.class);

        when(supporterDashboardModelPopulator.populateModel(loggedInUser)).thenReturn(expected);

        MvcResult result = mockMvc.perform(get("/supporter/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("supporter/supporter-dashboard"))
                .andReturn();

        SupporterDashboardViewModel actual = (SupporterDashboardViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(expected, actual);
    }
}