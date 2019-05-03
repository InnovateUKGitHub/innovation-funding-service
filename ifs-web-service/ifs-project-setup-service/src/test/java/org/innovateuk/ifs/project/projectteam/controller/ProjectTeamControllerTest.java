package org.innovateuk.ifs.project.projectteam.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.projectteam.populator.ProjectTeamViewModelPopulator;
import org.innovateuk.ifs.project.projectteam.viewmodel.ProjectOrganisationUserRowViewModel;
import org.innovateuk.ifs.project.projectteam.viewmodel.ProjectOrganisationViewModel;
import org.innovateuk.ifs.project.projectteam.viewmodel.ProjectTeamViewModel;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ProjectTeamControllerTest extends BaseControllerMockMVCTest<ProjectTeamController> {

    @Override
    protected ProjectTeamController supplyControllerUnderTest() {
        return new ProjectTeamController(populator);
    }

    @Mock
    private ProjectTeamViewModelPopulator populator;

    @Test
    public void viewProjectTeam() throws Exception {

        UserResource loggedInUser = newUserResource().build();
        setLoggedInUser(loggedInUser);

        long projectId = 999L;
        ProjectUserResource projectManager = newProjectUserResource().build();
        ProjectOrganisationUserRowViewModel user = new ProjectOrganisationUserRowViewModel
                ("test@test.test", "Steve Smith", 123L, true, true);
        ProjectOrganisationViewModel org = new ProjectOrganisationViewModel
                (singletonList(user), "Empire", 456L, true);
        ProjectTeamViewModel expected = new ProjectTeamViewModel
                ("Competition", "Project",
                 projectId, singletonList(org), org, org, projectManager,
                 true, loggedInUser.getId(), true,
                 false, true, false);

        when(populator.populate(projectId, loggedInUser)).thenReturn(expected);

        MvcResult result = mockMvc.perform(get("/project/{id}/team", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/team/project-team"))
                .andExpect(model().attributeDoesNotExist("readOnlyView"))
                .andReturn();

        ProjectTeamViewModel actual = (ProjectTeamViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(expected, actual);

    }
}
