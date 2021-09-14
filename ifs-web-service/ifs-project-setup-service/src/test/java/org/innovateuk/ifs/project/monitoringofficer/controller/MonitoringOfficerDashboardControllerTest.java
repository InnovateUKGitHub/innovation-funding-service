package org.innovateuk.ifs.project.monitoringofficer.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.monitoringofficer.populator.MonitoringOfficerDashboardViewModelPopulator;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerDashboardViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class MonitoringOfficerDashboardControllerTest extends BaseControllerMockMVCTest<MonitoringOfficerDashboardController> {

    @Mock
    private MonitoringOfficerDashboardViewModelPopulator populator;

    @Override
    protected MonitoringOfficerDashboardController supplyControllerUnderTest() {
        return new MonitoringOfficerDashboardController();
    }

    @Test
    public void viewDashboard() throws Exception {
        MonitoringOfficerDashboardViewModel model = mock(MonitoringOfficerDashboardViewModel.class);
        when(populator.populate(loggedInUser, null, true, false, false,
                false, false, false, false,
                false,0, 10)).thenReturn(model);

        mockMvc.perform(get("/monitoring-officer/dashboard"))
                .andExpect(view().name("monitoring-officer/dashboard"))
                .andExpect(model().attribute("model", model));
    }

    @Test
    public void filterDashboard() throws Exception {
        MonitoringOfficerDashboardViewModel model = mock(MonitoringOfficerDashboardViewModel.class);
        when(populator.populate(loggedInUser, "keyword", false, false, false,
                false, false, false, false,
                false,0, 10)).thenReturn(model);

        mockMvc.perform(post("/monitoring-officer/dashboard")
                .param("keywordSearch", "keyword"))
                .andExpect(view().name("monitoring-officer/dashboard"))
                .andExpect(model().attribute("model", model));
    }
}