package org.innovateuk.ifs.application.forms.questions.team.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.forms.questions.team.populator.ApplicationTeamPopulator;
import org.innovateuk.ifs.application.forms.questions.team.viewmodel.ApplicationTeamViewModel;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.Error.globalError;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ApplicationTeamControllerTest extends BaseControllerMockMVCTest<ApplicationTeamController> {

    @Override
    protected ApplicationTeamController supplyControllerUnderTest() {
        return new ApplicationTeamController();
    }

    @Mock
    private ApplicationTeamPopulator populator;

    @Mock
    private InviteRestService inviteRestService;

    @Mock
    private QuestionStatusRestService questionStatusRestService;

    @Mock
    private UserRestService userRestService;

    @Test
    public void viewTeam() throws Exception {
        long applicationId = 1L;
        long questionId = 2L;
        ApplicationTeamViewModel expected = mock(ApplicationTeamViewModel.class);

        when(populator.populate(applicationId, questionId, loggedInUser)).thenReturn(expected);

        MvcResult result = mockMvc.perform(get("/application/{applicationId}/form/question/{questionId}/team", applicationId, questionId))
                .andExpect(status().isOk())
                .andExpect(view().name("application/questions/application-team"))
                .andReturn();

        ApplicationTeamViewModel actual = (ApplicationTeamViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(expected, actual);
    }

    @Test
    public void markAsComplete() throws Exception {
        long applicationId = 1L;
        long questionId = 2L;

        ProcessRoleResource role = newProcessRoleResource().build();
        when(userRestService.findProcessRole(applicationId, loggedInUser.getId())).thenReturn(restSuccess(role));
        when(questionStatusRestService.markAsComplete(questionId, applicationId, role.getId())).thenReturn(restSuccess(emptyList()));

        MvcResult result = mockMvc.perform(post("/application/{applicationId}/form/question/{questionId}/team", applicationId, questionId)
                .param("complete", String.valueOf(true)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/application/%d/form/question/%d/team", applicationId, questionId)))
                .andReturn();

        verify(questionStatusRestService).markAsComplete(questionId, applicationId, role.getId());
    }

    @Test
    public void markAsComplete_Failure() throws Exception {
        long applicationId = 1L;
        long questionId = 2L;

        ProcessRoleResource role = newProcessRoleResource().build();
        when(userRestService.findProcessRole(applicationId, loggedInUser.getId())).thenReturn(restSuccess(role));
        when(questionStatusRestService.markAsComplete(questionId, applicationId, role.getId())).thenReturn(restSuccess(singletonList(new ValidationMessages(globalError("please.enter.some.text")))));
        when(questionStatusRestService.markAsInComplete(questionId, applicationId, role.getId())).thenReturn(restSuccess());
        MvcResult result = mockMvc.perform(post("/application/{applicationId}/form/question/{questionId}/team", applicationId, questionId)
                .param("complete", String.valueOf(true)))
                .andExpect(status().isOk())
                .andExpect(view().name("application/questions/application-team"))
                .andExpect(model().hasErrors())
                .andReturn();

        verify(questionStatusRestService).markAsComplete(questionId, applicationId, role.getId());
        verify(questionStatusRestService).markAsInComplete(questionId, applicationId, role.getId());
    }

    @Test
    public void markAsCompleteFailDueToPendingKta() throws Exception {
        long applicationId = 1L;
        long questionId = 2L;

        ProcessRoleResource role = newProcessRoleResource().build();
        when(userRestService.findProcessRole(applicationId, loggedInUser.getId())).thenReturn(restSuccess(role));
        when(questionStatusRestService.markAsComplete(questionId, applicationId, role.getId())).thenReturn(restSuccess(singletonList(new ValidationMessages(globalError("validation.kta.pending.invite")))));
        when(questionStatusRestService.markAsInComplete(questionId, applicationId, role.getId())).thenReturn(restSuccess());
        MvcResult result = mockMvc.perform(post("/application/{applicationId}/form/question/{questionId}/team", applicationId, questionId)
                .param("complete", String.valueOf(true)))
                .andExpect(status().isOk())
                .andExpect(view().name("application/questions/application-team"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode("form", "ktaEmail", "validation.kta.pending.invite"))
                .andReturn();

        verify(questionStatusRestService).markAsComplete(questionId, applicationId, role.getId());
        verify(questionStatusRestService).markAsInComplete(questionId, applicationId, role.getId());
    }

    @Test
    public void markAsCompleteFailDueToMissingKta() throws Exception {
        long applicationId = 1L;
        long questionId = 2L;

        ProcessRoleResource role = newProcessRoleResource().build();
        when(userRestService.findProcessRole(applicationId, loggedInUser.getId())).thenReturn(restSuccess(role));
        when(questionStatusRestService.markAsComplete(questionId, applicationId, role.getId())).thenReturn(restSuccess(singletonList(new ValidationMessages(globalError("validation.kta.missing.invite")))));
        when(questionStatusRestService.markAsInComplete(questionId, applicationId, role.getId())).thenReturn(restSuccess());
        MvcResult result = mockMvc.perform(post("/application/{applicationId}/form/question/{questionId}/team", applicationId, questionId)
                .param("complete", String.valueOf(true)))
                .andExpect(status().isOk())
                .andExpect(view().name("application/questions/application-team"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode("form", "ktaEmail", "validation.kta.missing.invite"))
                .andReturn();

        verify(questionStatusRestService).markAsComplete(questionId, applicationId, role.getId());
        verify(questionStatusRestService).markAsInComplete(questionId, applicationId, role.getId());
    }

    @Test
    public void showErrors() throws Exception {
        long applicationId = 1L;
        long questionId = 2L;

        ProcessRoleResource role = newProcessRoleResource().build();
        when(userRestService.findProcessRole(applicationId, loggedInUser.getId())).thenReturn(restSuccess(role));
        when(questionStatusRestService.markAsComplete(questionId, applicationId, role.getId())).thenReturn(restSuccess(emptyList()));

        MvcResult result = mockMvc.perform(get("/application/{applicationId}/form/question/{questionId}/team?show-errors=true", applicationId, questionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/application/%d/form/question/%d/team", applicationId, questionId)))
                .andReturn();

        verify(questionStatusRestService).markAsComplete(questionId, applicationId, role.getId());
    }

    @Test
    public void edit() throws Exception {
        long applicationId = 1L;
        long questionId = 2L;

        ProcessRoleResource role = newProcessRoleResource().build();
        when(userRestService.findProcessRole(applicationId, loggedInUser.getId())).thenReturn(restSuccess(role));
        when(questionStatusRestService.markAsInComplete(questionId, applicationId, role.getId())).thenReturn(restSuccess());

        MvcResult result = mockMvc.perform(post("/application/{applicationId}/form/question/{questionId}/team", applicationId, questionId)
                .param("edit", String.valueOf(true)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/application/%d/form/question/%d/team", applicationId, questionId)))
                .andReturn();

        verify(questionStatusRestService).markAsInComplete(questionId, applicationId, role.getId());
    }


    @Test
    public void openAddTeamMemberForm() throws Exception {
        long applicationId = 1L;
        long questionId = 2L;
        long organisationId = 3L;
        ApplicationTeamViewModel expected = mock(ApplicationTeamViewModel.class);

        when(populator.populate(applicationId, questionId, loggedInUser)).thenReturn(expected);
        when(expected.openAddTeamMemberForm(organisationId)).thenReturn(expected);

        MvcResult result = mockMvc.perform(post("/application/{applicationId}/form/question/{questionId}/team", applicationId, questionId)
                .param("add-team-member", String.valueOf(organisationId)))
                .andExpect(status().isOk())
                .andExpect(view().name("application/questions/application-team"))
                .andReturn();

        ApplicationTeamViewModel actual = (ApplicationTeamViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(expected, actual);
        verify(expected).openAddTeamMemberForm(organisationId);
    }

    @Test
    public void closeAddTeamMemberForm() throws Exception {
        long applicationId = 1L;
        long questionId = 2L;

        mockMvc.perform(post("/application/{applicationId}/form/question/{questionId}/team", applicationId, questionId)
                .param("close-add-team-member-form", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/application/%d/form/question/%d/team", applicationId, questionId)))
                .andReturn();
    }

    @Test
    public void removeUser() throws Exception {
        long applicationId = 1L;
        long questionId = 2L;
        long inviteId = 3L;

        when(inviteRestService.removeApplicationInvite(inviteId)).thenReturn(restSuccess());

        mockMvc.perform(post("/application/{applicationId}/form/question/{questionId}/team", applicationId, questionId)
                .param("remove-team-member", String.valueOf(inviteId)))
                .andExpect(status().is3xxRedirection());

        verify(inviteRestService).removeApplicationInvite(inviteId);
    }

    @Test
    public void removeInvite() throws Exception {
        long applicationId = 1L;
        long questionId = 2L;
        long inviteId = 3L;

        when(inviteRestService.removeApplicationInvite(inviteId)).thenReturn(restSuccess());

        mockMvc.perform(post("/application/{applicationId}/form/question/{questionId}/team", applicationId, questionId)
                .param("remove-invite", String.valueOf(inviteId)))
                .andExpect(status().is3xxRedirection());

        verify(inviteRestService).removeApplicationInvite(inviteId);
    }

    @Test
    public void resendInvite() throws Exception {
        long applicationId = 1L;
        long questionId = 2L;
        long inviteId = 3L;

        ApplicationInviteResource applicationInviteResource = newApplicationInviteResource()
                .withId(inviteId)
                .build();

        List<InviteOrganisationResource> inviteOrganisationResource = newInviteOrganisationResource()
                .withInviteResources(singletonList(applicationInviteResource))
                .build(1);

        when(inviteRestService.resendInvite(applicationInviteResource)).thenReturn(restSuccess());
        when(inviteRestService.getInvitesByApplication(applicationId)).thenReturn(restSuccess(inviteOrganisationResource));

        mockMvc.perform(post("/application/{applicationId}/form/question/{questionId}/team", applicationId, questionId)
                .param("resend-invite", String.valueOf(inviteId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/application/" + applicationId + "/form/question/" + questionId + "/team"));

        verify(inviteRestService).resendInvite(applicationInviteResource);
    }


    @Test
    public void inviteToExistingOrganisation() throws Exception {
        long applicationId = 1L;
        long questionId = 2L;
        long organisationId = 3L;
        String userName = "User";
        String email = "user@example.com";

        ApplicationInviteResource expectedInvite = newApplicationInviteResource()
                .withId((Long) null)
                .withApplication(applicationId)
                .withEmail(email)
                .withName(userName)
                .withInviteOrganisation(organisationId)
                .build();
        when(inviteRestService.createInvitesByOrganisationForApplication(applicationId, organisationId, singletonList(expectedInvite))).thenReturn(restSuccess());

        mockMvc.perform(post("/application/{applicationId}/form/question/{questionId}/team", applicationId, questionId)
                .param("invite-to-existing-organisation", String.valueOf(organisationId))
                .param("name", userName)
                .param("email", email))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/application/%d/form/question/%d/team", applicationId, questionId)))
                .andReturn();

        verify(inviteRestService).createInvitesByOrganisationForApplication(applicationId, organisationId, singletonList(expectedInvite));
    }

    @Test
    public void inviteToOrganisation() throws Exception {
        long applicationId = 1L;
        long questionId = 2L;
        long organisationId = 3L;
        String userName = "User";
        String email = "user@example.com";

        ApplicationInviteResource expectedInvite = newApplicationInviteResource()
                .withId((Long) null)
                .withApplication(applicationId)
                .withEmail(email)
                .withName(userName)
                .withInviteOrganisation(organisationId)
                .build();

        when(inviteRestService.saveInvites(singletonList(expectedInvite))).thenReturn(restSuccess());

        mockMvc.perform(post("/application/{applicationId}/form/question/{questionId}/team", applicationId, questionId)
                .param("invite-to-organisation", String.valueOf(organisationId))
                .param("name", userName)
                .param("email", email))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/application/%d/form/question/%d/team", applicationId, questionId)))
                .andReturn();

        verify(inviteRestService).saveInvites(singletonList(expectedInvite));
    }


}
