package org.innovateuk.ifs.project.status.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.junit.Test;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.builder.CompetitionProjectsStatusResourceBuilder.newCompetitionProjectsStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectStatusResourceBuilder.newProjectStatusResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StatusControllerTest extends BaseControllerMockMVCTest<StatusController> {

    @Test
    public void testGetCompetitionStatus() throws Exception {
        final Long competitionId = 123L;
        final CompetitionProjectsStatusResource cpsr = newCompetitionProjectsStatusResource().
                withCompetitionName("ABC").
                withCompetitionNumber(competitionId).
                withProjectStatusResources(newProjectStatusResource().withProjectNumber().build(3)).build();
        ServiceResult<CompetitionProjectsStatusResource> expected = serviceSuccess(cpsr);
        when(statusServiceMock.getCompetitionStatus(competitionId)).thenReturn(expected);

        mockMvc.perform(get("/project/competition/{competitionId}", 123L)).
                andExpect(status().isOk()).
                andExpect(content().json(toJson(cpsr)));

        verify(statusServiceMock).getCompetitionStatus(competitionId);
    }

    @Test
    public void projectControllerShouldReturnStatusByProjectId() throws Exception {
        Long projectId = 2L;

        ProjectStatusResource projectStatusResource = newProjectStatusResource().build();

        when(statusServiceMock.getProjectStatusByProjectId(projectId)).thenReturn(serviceSuccess(projectStatusResource));

        mockMvc.perform(get("/project/{id}/status", projectId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(projectStatusResource)));
    }

    @Override
    protected StatusController supplyControllerUnderTest() {
        return new StatusController();
    }
}
