package org.innovateuk.ifs.project.monitoringofficer.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.monitoringofficer.form.MODashboardForm;
import org.innovateuk.ifs.project.monitoringofficer.populator.MonitoringOfficerDashboardViewModelPopulator;
import org.innovateuk.ifs.project.monitoringofficer.service.MonitoringOfficerDashBoardCookieService;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerDashboardViewModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class MonitoringOfficerDashboardControllerTest extends BaseControllerMockMVCTest<MonitoringOfficerDashboardController> {

    @Mock
    private MonitoringOfficerDashboardViewModelPopulator populator;

    @Mock
    private MonitoringOfficerDashBoardCookieService cookieService;

    private MODashboardForm monitoringOfficerDashboardForm;

    @Override
    protected MonitoringOfficerDashboardController supplyControllerUnderTest() {
        return new MonitoringOfficerDashboardController();
    }

    @Before
    public void setUpForm() {
        monitoringOfficerDashboardForm = new MODashboardForm();
    }
    @Test
    public void viewDashboard() throws Exception {
        MonitoringOfficerDashboardViewModel model = mock(MonitoringOfficerDashboardViewModel.class);

        when(cookieService.getMODashboardFormCookieValue(any(), any(), any())).thenReturn(monitoringOfficerDashboardForm);
        when(populator.populate(loggedInUser, null, true, false, false,
                false, false, false, false,
                false,0, 10)).thenReturn(model);
        mockMvc.perform(get("/monitoring-officer/dashboard"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("monitoring-officer/dashboard"))
                .andExpect(model().attributeExists("form"));
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