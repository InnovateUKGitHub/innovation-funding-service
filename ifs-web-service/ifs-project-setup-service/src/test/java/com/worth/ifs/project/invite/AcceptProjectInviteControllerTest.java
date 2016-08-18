package com.worth.ifs.project.invite;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.invite.resource.InviteProjectResource;
import com.worth.ifs.invite.service.ProjectInviteRestService;
import com.worth.ifs.project.viewmodel.JoinAProjectViewModel;
import com.worth.ifs.registration.service.AcceptProjectInviteController;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;

import static com.worth.ifs.BaseControllerMockMVCTest.setupMockMvc;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.invite.builder.ProjectInviteResourceBuilder.newInviteProjectResource;
import static com.worth.ifs.invite.constant.InviteStatusConstants.ACCEPTED;
import static com.worth.ifs.invite.constant.InviteStatusConstants.SEND;
import static com.worth.ifs.registration.service.AcceptProjectInviteController.*;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class AcceptProjectInviteControllerTest extends BaseUnitTest {

    @InjectMocks
    private AcceptProjectInviteController acceptProjectInviteController;

    @Mock
    private ProjectInviteRestService projectInviteRestServiceMock;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = setupMockMvc(acceptProjectInviteController, () -> loggedInUser, env, messageSource);
    }

    @Test
    public void testFailureUserExistsButIsLoggedInWithTheWrongUser() throws Exception {
        loggedInUser = newUserResource().withEmail("loggedInUser@example.com").build();
        UserResource inviteUser = newUserResource().withEmail("inviteUser@example.com").build();
        InviteProjectResource invite = newInviteProjectResource().withHash("hash").withStatus(SEND).withEmail(inviteUser.getEmail()).build();
        when(projectInviteRestServiceMock.getInviteByHash(invite.getHash())).thenReturn(restSuccess(invite));
        when(projectInviteRestServiceMock.checkExistingUser(invite.getHash())).thenReturn(restSuccess(true));
        mockMvc.perform(get(ACCEPT_INVITE_MAPPING + invite.getHash()))
                .andExpect(status().is2xxSuccessful())
                        // Currently we tell them to log out and try again as there is not currently a way of forcing this
                .andExpect(cookie().doesNotExist(AcceptProjectInviteController.INVITE_HASH))
                .andExpect(view().name(AcceptProjectInviteController.ACCEPT_INVITE_FAILURE));
    }

    @Test
    public void testFailureInviteAlreadyAccepted() throws Exception {
        UserResource inviteUser = newUserResource().withEmail("email@example.com").build();
        loggedInUser = inviteUser;
        InviteProjectResource invite = newInviteProjectResource().withHash("hash").withStatus(ACCEPTED).withEmail(inviteUser.getEmail()).build();
        when(projectInviteRestServiceMock.getInviteByHash(invite.getHash())).thenReturn(restSuccess(invite));
        when(projectInviteRestServiceMock.checkExistingUser(invite.getHash())).thenReturn(restSuccess(true));
        mockMvc.perform(get(ACCEPT_INVITE_MAPPING + invite.getHash()))
                .andExpect(status().is2xxSuccessful())
                        // Currently we tell them to log out and try again as there is not currently a way of forcing this
                .andExpect(cookie().doesNotExist(AcceptProjectInviteController.INVITE_HASH))
                .andExpect(view().name(AcceptProjectInviteController.ACCEPT_INVITE_FAILURE));
    }

    @Test
    public void testInviteEntryUserExistsButIsNotLoggedIn() throws Exception {
        loggedInUser = null; // No one logged in.
        UserResource user = newUserResource().withEmail("email@example.com").build();
        InviteProjectResource invite = newInviteProjectResource().withHash("hash").withStatus(SEND).withEmail(user.getEmail()).build();
        when(projectInviteRestServiceMock.getInviteByHash(invite.getHash())).thenReturn(restSuccess(invite));
        when(projectInviteRestServiceMock.checkExistingUser(invite.getHash())).thenReturn(restSuccess(true));
        mockMvc.perform(get(ACCEPT_INVITE_MAPPING + invite.getHash()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(cookie().exists(AcceptProjectInviteController.INVITE_HASH))
                .andExpect(cookie().value(AcceptProjectInviteController.INVITE_HASH, invite.getHash()))
                .andExpect(view().name(ACCEPT_INVITE_USER_EXISTS_BUT_NOT_LOGGED_IN_VIEW));
    }


    @Test
    public void testInviteEntryUserExistsAndIsLoggedIn() throws Exception {
        UserResource inviteUser = newUserResource().withEmail("inviteUser@example.com").build();
        loggedInUser = inviteUser;
        InviteProjectResource invite = newInviteProjectResource().withHash("hash").withStatus(SEND).withEmail(inviteUser.getEmail()).build();
        when(projectInviteRestServiceMock.getInviteByHash(invite.getHash())).thenReturn(restSuccess(invite));
        when(projectInviteRestServiceMock.checkExistingUser(invite.getHash())).thenReturn(restSuccess(true));
        mockMvc.perform(get(ACCEPT_INVITE_MAPPING + invite.getHash()))
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists(AcceptProjectInviteController.INVITE_HASH))
                .andExpect(cookie().value(AcceptProjectInviteController.INVITE_HASH, invite.getHash()))
                .andExpect(redirectedUrl(ACCEPT_INVITE_USER_EXIST_SHOW_PROJECT_MAPPING));
    }

    @Test
    public void testInviteEntryUserDoesNotExistAndNotLoggedIn() throws Exception {
        loggedInUser = null;
        InviteProjectResource invite = newInviteProjectResource().withHash("hash").withStatus(SEND).withEmail("doesNotYetExist@example.com").build();
        when(projectInviteRestServiceMock.getInviteByHash(invite.getHash())).thenReturn(restSuccess(invite));
        when(projectInviteRestServiceMock.checkExistingUser(invite.getHash())).thenReturn(restSuccess(false));
        mockMvc.perform(get(ACCEPT_INVITE_MAPPING + invite.getHash()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ACCEPT_INVITE_USER_DOES_NOT_YET_EXIST_SHOW_PROJECT_MAPPING));
    }

    @Test
    public void testAcceptInviteUserDoesNotYetExistShowProject() throws Exception {
        loggedInUser = null;
        OrganisationResource organisation = newOrganisationResource().build();
        InviteProjectResource invite = newInviteProjectResource().withHash("hash").withStatus(SEND).withEmail("doesNotYetExist@example.com").withProjectName("project name").withOrganisation(organisation.getId()).build();
        when(projectInviteRestServiceMock.getInviteByHash(invite.getHash())).thenReturn(restSuccess(invite));
        when(projectInviteRestServiceMock.checkExistingUser(invite.getHash())).thenReturn(restSuccess(false));
        when(organisationRestService.getOrganisationByIdForAnonymousUserFlow(invite.getOrganisation())).thenReturn(restSuccess(organisation));
        MvcResult mvcResult = mockMvc.perform(get(ACCEPT_INVITE_USER_DOES_NOT_YET_EXIST_SHOW_PROJECT_MAPPING).cookie(new Cookie(AcceptProjectInviteController.INVITE_HASH, invite.getHash())))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name(ACCEPT_INVITE_SHOW_PROJECT)).andReturn();
        JoinAProjectViewModel model = (JoinAProjectViewModel)mvcResult.getModelAndView().getModel().get("model");
        Boolean userExists = (Boolean)mvcResult.getModelAndView().getModel().get("userExists");
        assertEquals(model.getProjectName(), invite.getProjectName());
        assertFalse(model.getProjectName(), userExists);
    }

    @Test
    public void testAcceptInviteUserDoesExistShowProject() throws Exception {
        UserResource inviteUser = newUserResource().withEmail("doesExist@example.com").build();
        loggedInUser = inviteUser;
        OrganisationResource organisation = newOrganisationResource().build();
        InviteProjectResource invite = newInviteProjectResource().withHash("hash").withStatus(SEND).withEmail(inviteUser.getEmail()).withProjectName("project name").withOrganisation(organisation.getId()).build();
        when(projectInviteRestServiceMock.getInviteByHash(invite.getHash())).thenReturn(restSuccess(invite));
        when(projectInviteRestServiceMock.checkExistingUser(invite.getHash())).thenReturn(restSuccess(true));
        when(organisationRestService.getOrganisationByIdForAnonymousUserFlow(invite.getOrganisation())).thenReturn(restSuccess(organisation));
        MvcResult mvcResult = mockMvc.perform(get(ACCEPT_INVITE_USER_EXIST_SHOW_PROJECT_MAPPING).cookie(new Cookie(AcceptProjectInviteController.INVITE_HASH, invite.getHash())))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name(ACCEPT_INVITE_SHOW_PROJECT)).andReturn();
        JoinAProjectViewModel model = (JoinAProjectViewModel)mvcResult.getModelAndView().getModel().get("model");
        Boolean userExists = (Boolean)mvcResult.getModelAndView().getModel().get("userExists");
        assertEquals(model.getProjectName(), invite.getProjectName());
        assertTrue(model.getProjectName(), userExists);
    }
}