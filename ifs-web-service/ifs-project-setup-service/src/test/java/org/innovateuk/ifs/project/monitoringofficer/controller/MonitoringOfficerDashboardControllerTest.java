package org.innovateuk.ifs.project.monitoringofficer.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.monitoringofficer.form.MonitoringOfficerDashboardForm;
import org.innovateuk.ifs.project.monitoringofficer.populator.MonitoringOfficerDashboardViewModelPopulator;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerDashboardViewModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class MonitoringOfficerDashboardControllerTest extends BaseControllerMockMVCTest<MonitoringOfficerDashboardController> {

    @Mock
    private MonitoringOfficerDashboardViewModelPopulator populator;

    private MonitoringOfficerDashboardForm monitoringOfficerDashboardForm;

    @Override
    protected MonitoringOfficerDashboardController supplyControllerUnderTest() {
        return new MonitoringOfficerDashboardController();
    }

    @Before
    public void setUpForm() {
        monitoringOfficerDashboardForm = new MonitoringOfficerDashboardForm();
    }
    @Test
    public void viewDashboard() throws Exception {
        MonitoringOfficerDashboardViewModel model = mock(MonitoringOfficerDashboardViewModel.class);

        when(populator.populate(loggedInUser,monitoringOfficerDashboardForm,0, 10)).thenReturn(model);
        mockMvc.perform(get("/monitoring-officer/dashboard"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("monitoring-officer/dashboard"))
                .andExpect(model().attributeExists("form"));
    }

    @Test
    public void filterDashboard() throws Exception {
        MonitoringOfficerDashboardViewModel model = mock(MonitoringOfficerDashboardViewModel.class);
        monitoringOfficerDashboardForm.setKeywordSearch("keyword");
        when(populator.populate(loggedInUser, monitoringOfficerDashboardForm,0, 10)).thenReturn(model);

        mockMvc.perform(post("/monitoring-officer/dashboard")
                .param("keywordSearch", "keyword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/monitoring-officer/dashboard/results?keywordSearch=keyword&inSetup=false&inPrevious=false"));

    }
}