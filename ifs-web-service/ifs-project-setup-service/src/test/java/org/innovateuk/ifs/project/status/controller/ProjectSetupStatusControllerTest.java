package org.innovateuk.ifs.project.status.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.status.populator.ProjectSetupStatusViewModelPopulator;
import org.innovateuk.ifs.project.status.viewmodel.ProjectSetupStatusViewModel;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ProjectSetupStatusControllerTest extends BaseControllerMockMVCTest<ProjectSetupStatusController> {

    @Override
    protected ProjectSetupStatusController supplyControllerUnderTest() {
        return new ProjectSetupStatusController();
    }

    @Mock
    private ProjectSetupStatusViewModelPopulator populator;

    @Test
    public void viewProjectSetupStatusSuccess() throws Exception {

        Long projectId = 1L;

        when(populator.populateViewModel(projectId, loggedInUser)).thenReturn(new ProjectSetupStatusViewModel());

        mockMvc.perform(get("/project/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/setup-status"));
    }
}
