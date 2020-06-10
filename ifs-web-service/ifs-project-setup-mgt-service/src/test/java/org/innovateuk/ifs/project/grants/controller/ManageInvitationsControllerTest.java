package org.innovateuk.ifs.project.grants.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.grants.service.GrantsInviteRestService;
import org.innovateuk.ifs.grantsinvite.resource.SentGrantsInviteResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.grants.populator.ManageInvitationsModelPopulator;
import org.innovateuk.ifs.project.grants.viewmodel.ManageInvitationsViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.resource.ProjectState.SETUP;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ManageInvitationsControllerTest  extends BaseControllerMockMVCTest<ManageInvitationsController> {

    @Mock
    private ProjectService projectService;

    @Mock
    private GrantsInviteRestService grantsInviteRestService;

    @Mock
    private ManageInvitationsModelPopulator manageInvitationsModelPopulator;

    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Override
    protected ManageInvitationsController supplyControllerUnderTest() {
        return new ManageInvitationsController();
    }

    @Test
    public void manageInvitations() throws Exception {
        long competitionId = 1L;
        long applicationId = 2L;
        long projectId = 123L;
        ProjectResource project = newProjectResource()
                .withId(projectId)
                .withName("Name")
                .withCompetition(competitionId)
                .withApplication(applicationId)
                .withProjectState(SETUP).build();
        when(projectService.getById(projectId)).thenReturn(project);

        List<SentGrantsInviteResource> grants = new ArrayList<>();
        when(grantsInviteRestService.getAllForProject(projectId)).thenReturn(restSuccess(grants));

        ManageInvitationsViewModel viewModel = new ManageInvitationsViewModel(competitionId, null, projectId, null, applicationId, grants);
        when(manageInvitationsModelPopulator.populateManageInvitationsViewModel(project, grants)).thenReturn(viewModel);

        MvcResult result = mockMvc.perform(get("/project/{projectId}/grants/invite", projectId))
                .andExpect(view().name("project/manage-invitations"))
                .andReturn();

        ManageInvitationsViewModel actualViewModel = (ManageInvitationsViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(viewModel, actualViewModel);
    }

    @Test
    public void resendInvitation() throws Exception {
        long projectId = 123L;
        long inviteId = 567L;

        mockMvc.perform(post("/project/{projectId}/grants/invite/resend", projectId)
                .param("projectId", Long.toString(projectId))
                .param("inviteId", Long.toString(inviteId)))
                .andExpect(redirectedUrl("/project/123/grants/invite"));

        verify(grantsInviteRestService).resendInvite(projectId, inviteId);
        verify(cookieFlashMessageFilter).setFlashMessage(any(), eq("emailSent"));
    }
}
