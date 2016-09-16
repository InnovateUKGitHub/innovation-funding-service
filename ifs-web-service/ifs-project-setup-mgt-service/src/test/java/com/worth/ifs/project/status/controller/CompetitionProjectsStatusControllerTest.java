package com.worth.ifs.project.status.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.project.resource.CompetitionProjectsStatusResource;
import org.junit.Test;

import static com.worth.ifs.project.builder.CompetitionProjectsStatusResourceBuilder.newCompetitionProjectsStatusResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class CompetitionProjectsStatusControllerTest extends BaseControllerMockMVCTest<CompetitionProjectsStatusController> {

    @Test
    public void testViewCompetitionStatusPage() throws Exception {
        Long competitionId = 123L;

        CompetitionProjectsStatusResource expected = newCompetitionProjectsStatusResource().build();

        when(projectStatusServiceMock.getCompetitionStatus(competitionId)).thenReturn(expected);

        mockMvc.perform(get("/competition/123/status")).
                andExpect(view().name("project/competition-status")).
                andExpect(model().attribute("model", expected)).
                andReturn();
    }

    @Override
    protected CompetitionProjectsStatusController supplyControllerUnderTest() {
        return new CompetitionProjectsStatusController();
    }
}
