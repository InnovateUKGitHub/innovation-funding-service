package com.worth.ifs.project.status.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.project.status.resource.CompetitionProjectsStatusResource;
import com.worth.ifs.project.status.viewmodel.CompetitionProjectStatusViewModel;
import org.junit.Test;

import static com.worth.ifs.project.builder.CompetitionProjectsStatusResourceBuilder.newCompetitionProjectsStatusResource;
import static org.hamcrest.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class CompetitionProjectsStatusControllerTest extends BaseControllerMockMVCTest<CompetitionProjectsStatusController> {

    @Test
    public void testViewCompetitionStatusPage() throws Exception {
        Long competitionId = 123L;

        CompetitionProjectsStatusResource competitionProjectsStatus = newCompetitionProjectsStatusResource().build();

        when(projectStatusServiceMock.getCompetitionStatus(competitionId)).thenReturn(competitionProjectsStatus);

        mockMvc.perform(get("/competition/" + competitionId + "/status"))
                .andExpect(view().name("project/competition-status"))
                .andExpect(model().attribute("model", any(CompetitionProjectStatusViewModel.class)))
                .andReturn();
    }

    @Override
    protected CompetitionProjectsStatusController supplyControllerUnderTest() {
        return new CompetitionProjectsStatusController();
    }
}
