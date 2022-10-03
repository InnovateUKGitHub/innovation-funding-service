package org.innovateuk.ifs.project.status.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.status.populator.SetupStatusViewModelPopulator;
import org.innovateuk.ifs.project.status.viewmodel.SetupStatusViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class SetupStatusControllerTest extends BaseControllerMockMVCTest<SetupStatusController> {

    @Override
    protected SetupStatusController supplyControllerUnderTest() {
        return new SetupStatusController();
    }

    @Mock
    private SetupStatusViewModelPopulator populator;

    @Test
    public void viewProjectSetupStatusSuccess() throws Exception {
        long projectId = 1L;
        SetupStatusViewModel viewModel = mock(SetupStatusViewModel.class);
        when(populator.populateViewModel(projectId, loggedInUser)).thenReturn(viewModel);

        mockMvc.perform(get("/project/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/setup-status"))
                .andExpect(model().attribute("model", viewModel));
    }
}
