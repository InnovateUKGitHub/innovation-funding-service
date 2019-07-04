package org.innovateuk.ifs.project.activitylog.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.activitylog.populator.ActivityLogViewModelPopulator;
import org.innovateuk.ifs.project.activitylog.viewmodel.ActivityLogViewModel;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ActivityLogControllerTest extends BaseControllerMockMVCTest<ActivityLogController> {

    @Mock
    private ActivityLogViewModelPopulator activityLogViewModelPopulator;

    @Test
    public void viewActivityLog() throws Exception {
        long competitionId = 123L;
        long projectId = 1L;

        ActivityLogViewModel viewModel = mock(ActivityLogViewModel.class);
        when(activityLogViewModelPopulator.populate(projectId)).thenReturn(viewModel);

        mockMvc.perform(get("/competition/{competitionId}/project/{projectId}/activity-log", competitionId, projectId))
                .andExpect(view().name("project/activity-log"))
                .andExpect(model().attribute("model", viewModel));
    }

    @Override
    protected ActivityLogController supplyControllerUnderTest() {
        return new ActivityLogController();
    }
}
