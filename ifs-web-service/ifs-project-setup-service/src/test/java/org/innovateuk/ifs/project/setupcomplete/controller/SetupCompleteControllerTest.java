package org.innovateuk.ifs.project.setupcomplete.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.setupcomplete.SetupCompleteController;
import org.innovateuk.ifs.project.setupcomplete.populator.SetupCompleteViewModelPopulator;
import org.innovateuk.ifs.project.setupcomplete.viewmodel.SetupCompleteViewModel;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class SetupCompleteControllerTest extends BaseControllerMockMVCTest<SetupCompleteController> {

    @Mock
    private SetupCompleteViewModelPopulator setupCompleteViewModelPopulatorMock;

    @Override
    protected SetupCompleteController supplyControllerUnderTest() {
        return new SetupCompleteController();
    }

    @Test
    public void viewProjectSetupComplete() throws Exception {

        long projectId = 1L;

        SetupCompleteViewModel viewModel = mock(SetupCompleteViewModel.class);
        when(setupCompleteViewModelPopulatorMock.populate(projectId)).thenReturn(viewModel);

        mockMvc.perform(get("/project/{projectId}/setup", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/project-setup-complete"));
    }
}
