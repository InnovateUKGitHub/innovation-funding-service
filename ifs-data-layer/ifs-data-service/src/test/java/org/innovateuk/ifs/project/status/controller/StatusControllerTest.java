package org.innovateuk.ifs.project.status.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.status.resource.ProjectStatusPageResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.project.status.transactional.InternalUserProjectStatusService;
import org.innovateuk.ifs.project.status.transactional.StatusService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.builder.ProjectStatusResourceBuilder.newProjectStatusResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StatusControllerTest extends BaseControllerMockMVCTest<StatusController> {

    @Mock
    private StatusService statusServiceMock;

    @Mock
    private InternalUserProjectStatusService internalUserProjectStatusService;

    @Test
    public void getCompetitionStatus() throws Exception {
        final Long competitionId = 123L;
        String applicationSearchString = "12";
        ServiceResult<ProjectStatusPageResource> expected = serviceSuccess(new ProjectStatusPageResource());
        when(internalUserProjectStatusService.getCompetitionStatus(competitionId, applicationSearchString, 0, 5)).thenReturn(expected);

        mockMvc.perform(get("/project/competition/{competitionId}?applicationSearchString=" + applicationSearchString, 123L)).
                andExpect(status().isOk());

        verify(internalUserProjectStatusService).getCompetitionStatus(competitionId, applicationSearchString, 0, 5);
    }

    @Test
    public void projectControllerShouldReturnStatusByProjectId() throws Exception {
        Long projectId = 2L;

        ProjectStatusResource projectStatusResource = newProjectStatusResource().build();

        when(internalUserProjectStatusService.getProjectStatusByProjectId(projectId)).thenReturn(serviceSuccess(projectStatusResource));

        mockMvc.perform(get("/project/{id}/status", projectId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(projectStatusResource)));
    }

    @Override
    protected StatusController supplyControllerUnderTest() {
        return new StatusController();
    }
}
