package org.innovateuk.ifs.project.status.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.status.populator.SetupStatusViewModelPopulator;
import org.innovateuk.ifs.project.status.viewmodel.SetupStatusViewModel;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class SetupStatusControllerTest extends BaseControllerMockMVCTest<SetupStatusController> {

    @Override
    protected SetupStatusController supplyControllerUnderTest() {
        return new SetupStatusController();
    }

    @Mock
    private SetupStatusViewModelPopulator populator;

    @Test
    public void viewProjectSetupStatusSuccess() throws Exception {

        Long projectId = 1L;

        when(populator.populateViewModel(projectId, loggedInUser)).thenReturn(new SetupStatusViewModel());

        mockMvc.perform(get("/project/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/setup-status"));
    }
}
