package com.worth.ifs.project;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.project.builder.ProjectLeadStatusResourceBuilder;
import com.worth.ifs.project.consortiumoverview.viewmodel.ProjectConsortiumStatusViewModel;
import com.worth.ifs.project.resource.ProjectPartnerStatusResource;
import com.worth.ifs.project.resource.ProjectTeamStatusResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.worth.ifs.project.builder.ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource;
import static com.worth.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.class)
public class ProjectTeamStatusControllerTest extends BaseControllerMockMVCTest<ProjectTeamStatusController> {

    @Before
    public void setUp() {
        super.setUp();
        setupInvites();
        loginDefaultUser();
        loggedInUser.setOrganisations(Collections.singletonList(8L));
    }

    @Override
    protected ProjectTeamStatusController supplyControllerUnderTest() {
        return new ProjectTeamStatusController();
    }

    @Test
    public void testViewProjectTeamStatus() throws Exception {
        Long projectId = 1L;

        ProjectTeamStatusResource expectedTeamStatus = buildTeamStatus();

        when(projectService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(expectedTeamStatus);

        ProjectConsortiumStatusViewModel expected = new ProjectConsortiumStatusViewModel(projectId, expectedTeamStatus);

        MvcResult mvcResult = mockMvc.perform(get("/project/{id}/team-status", projectId))
            .andExpect(view().name("project/consortium-status"))
            .andExpect(model().attribute("model", expected)).andReturn();

        Map<String, Object> model = mvcResult.getModelAndView().getModel();
        assertEquals(expected, model.get("model"));
    }

    private ProjectTeamStatusResource buildTeamStatus(){
        List<ProjectPartnerStatusResource> partnerStatuses = newProjectPartnerStatusResource().build(2);
        ProjectPartnerStatusResource leadProjectPartnerStatusResource = ProjectLeadStatusResourceBuilder.newProjectLeadStatusResource().build();
        partnerStatuses.add(leadProjectPartnerStatusResource);
        return newProjectTeamStatusResource().withPartnerStatuses(partnerStatuses).build();
    }
}
