package org.innovateuk.ifs.project.managestate.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.managestate.viewmodel.OnHoldViewModel;
import org.innovateuk.ifs.project.managestate.viewmodel.ProjectStateCommentViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.project.service.ProjectStateRestService;
import org.innovateuk.ifs.threads.resource.PostResource;
import org.innovateuk.ifs.threads.resource.ProjectStateCommentsResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import java.time.ZonedDateTime;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.resource.ProjectState.ON_HOLD;
import static org.innovateuk.ifs.project.resource.ProjectState.SETUP;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.TimeZoneUtil.toUkTimeZone;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OnHoldControllerTest extends BaseControllerMockMVCTest<OnHoldController> {

    @Mock
    private ProjectRestService projectRestService;

    @Mock
    private ProjectStateRestService projectStateRestService;

    @Override
    protected OnHoldController supplyControllerUnderTest() {
        return new OnHoldController();
    }

    @Test
    public void viewOnHoldStatus() throws Exception {
        long competitionId = 1L;
        long applicationId = 2L;
        long projectId = 123L;
        ProjectResource project = newProjectResource()
                .withId(projectId)
                .withName("Name")
                .withCompetition(competitionId)
                .withApplication(applicationId)
                .withProjectState(ON_HOLD).build();
        UserResource author = newUserResource()
                .withRoleGlobal(Role.IFS_ADMINISTRATOR)
                .withFirstName("Bob")
                .withLastName("Someone")
                .build();
        ZonedDateTime created = ZonedDateTime.now();

        ProjectStateCommentsResource commentsResource = new ProjectStateCommentsResource(1L, projectId,
                singletonList(new PostResource(2L, author, "Body", emptyList(), created)), ON_HOLD, "Title", created, author, null);

        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(project));
        when(projectStateRestService.findOpenComments(projectId)).thenReturn(restSuccess(commentsResource));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/project/{projectId}/on-hold-status", competitionId, projectId))
                .andExpect(view().name("project/on-hold-status"))
                .andReturn();

        OnHoldViewModel viewModel = (OnHoldViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(competitionId, viewModel.getCompetitionId());
        assertEquals(projectId, viewModel.getProjectId());
        assertEquals(applicationId, viewModel.getApplicationId());
        assertEquals("Name", viewModel.getProjectName());

        assertEquals("Title", viewModel.getTitle());
        assertEquals(1, viewModel.getComments().size());

        ProjectStateCommentViewModel commentViewModel = viewModel.getComments().get(0);

        assertEquals("Bob Someone", commentViewModel.getUser());
        assertEquals("Innovate UK (IFS Administrator)", commentViewModel.getUserRole());
        assertEquals("Body", commentViewModel.getComment());
        assertEquals(toUkTimeZone(created), commentViewModel.getDate());
    }

    @Test
    public void viewOnHoldStatus_redirectNotOnHold() throws Exception {
        long competitionId = 1L;
        long applicationId = 2L;
        long projectId = 123L;
        ProjectResource project = newProjectResource()
                .withId(projectId)
                .withName("Name")
                .withCompetition(competitionId)
                .withApplication(applicationId)
                .withProjectState(SETUP).build();

        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(project));

        mockMvc.perform(get("/competition/{competitionId}/project/{projectId}/on-hold-status", competitionId, projectId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/competition/%d/project/%d/manage-status", competitionId, projectId)));
    }

    @Test
    public void resumeProject_admin() throws Exception {
        long competitionId = 1L;
        long projectId = 123L;

        setLoggedInUser(newUserResource()
                .withRoleGlobal(Role.IFS_ADMINISTRATOR)
                .build());

        when(projectStateRestService.resumeProject(projectId)).thenReturn(restSuccess());

        mockMvc.perform(post("/competition/{competitionId}/project/{projectId}/on-hold-status", competitionId, projectId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/competition/%d/project/%d/manage-status?resumedFromOnHold=true", competitionId, projectId)));

        verify(projectStateRestService).resumeProject(projectId);
    }

    @Test
    public void resumeProject_projectFinance() throws Exception {
        long competitionId = 1L;
        long projectId = 123L;

        setLoggedInUser(newUserResource()
                .withRoleGlobal(Role.PROJECT_FINANCE)
                .build());

        when(projectStateRestService.resumeProject(projectId)).thenReturn(restSuccess());

        mockMvc.perform(post("/competition/{competitionId}/project/{projectId}/on-hold-status", competitionId, projectId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/competition/%d/project/%d/details?resumedFromOnHold=true", competitionId, projectId)));

        verify(projectStateRestService).resumeProject(projectId);
    }

    @Test
    public void addComment_success() throws Exception {
        long competitionId = 1L;
        long projectId = 123L;
        long commentId = 2L;
        String details = "details";

        when(projectStateRestService.addPost(any(PostResource.class), eq(projectId), eq(commentId))).thenReturn(restSuccess());

        mockMvc.perform(post("/competition/{competitionId}/project/{projectId}/on-hold-status", competitionId, projectId)
                .param("details", details)
                .param("commentId", String.valueOf(commentId))
                .param("add-comment", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/competition/%d/project/%d/on-hold-status", competitionId, projectId)));


        ArgumentCaptor<PostResource> argumentCaptor = ArgumentCaptor.forClass(PostResource.class);
        verify(projectStateRestService).addPost(argumentCaptor.capture(), eq(projectId), eq(commentId));
        PostResource post = argumentCaptor.getValue();

        assertEquals(post.body, details);
        assertEquals(post.author, getLoggedInUser());
    }

    @Test
    public void addComment_validation() throws Exception {
        long competitionId = 1L;
        long applicationId = 2L;
        long commentId = 2L;
        long projectId = 123L;
        ProjectResource project = newProjectResource()
                .withId(projectId)
                .withName("Name")
                .withCompetition(competitionId)
                .withApplication(applicationId)
                .withProjectState(ON_HOLD).build();
        UserResource author = newUserResource()
                .withRoleGlobal(Role.IFS_ADMINISTRATOR)
                .withFirstName("Bob")
                .withLastName("Someone")
                .build();
        ZonedDateTime created = ZonedDateTime.now();

        ProjectStateCommentsResource commentsResource = new ProjectStateCommentsResource(1L, projectId,
                singletonList(new PostResource(2L, author, "Body", emptyList(), created)), ON_HOLD, "Title", created, author, null);

        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(project));
        when(projectStateRestService.findOpenComments(projectId)).thenReturn(restSuccess(commentsResource));

        mockMvc.perform(post("/competition/{competitionId}/project/{projectId}/on-hold-status", competitionId, projectId)
                .param("details", "")
                .param("commentId", String.valueOf(commentId))
                .param("add-comment", "true"))
                .andExpect(view().name("project/on-hold-status"))
                .andExpect(model().attributeHasFieldErrorCode("form", "details", "NotBlank"));
    }

}
