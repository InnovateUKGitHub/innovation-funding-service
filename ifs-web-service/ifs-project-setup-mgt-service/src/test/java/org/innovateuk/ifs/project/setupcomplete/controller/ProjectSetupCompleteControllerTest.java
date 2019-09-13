package org.innovateuk.ifs.project.setupcomplete.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.project.service.ProjectStateRestService;
import org.innovateuk.ifs.project.setupcomplete.viewmodel.ProjectSetupCompleteViewModel;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ProjectSetupCompleteControllerTest extends BaseControllerMockMVCTest<ProjectSetupCompleteController> {

    @Mock
    private ProjectRestService projectRestService;

    @Mock
    private ProjectStateRestService projectStateRestService;

    @Override
    protected ProjectSetupCompleteController supplyControllerUnderTest() {
        return new ProjectSetupCompleteController();
    }

    @Test
    public void viewSetupCompletePage() throws Exception {
        long competitionId = 1L;
        long projectId = 2L;

        ProjectResource project = newProjectResource().withId(projectId).build();

        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(project));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/project/{projectId}/setup-complete", competitionId, projectId))
                .andExpect(view().name("blah"))
                .andReturn();

        ProjectSetupCompleteViewModel viewModel = (ProjectSetupCompleteViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(viewModel.getProjectId(), projectId);
    }
}
