package org.innovateuk.ifs.project.invite;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.invite.service.ProjectInviteRestService;
import org.innovateuk.ifs.project.projectdetails.viewmodel.JoinAProjectViewModel;
import org.innovateuk.ifs.registration.service.AcceptProjectInviteController;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;

import static org.innovateuk.ifs.BaseControllerMockMVCTest.setupMockMvc;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.invite.builder.ProjectInviteResourceBuilder.newInviteProjectResource;
import static org.innovateuk.ifs.invite.constant.InviteStatus.*;
import static org.innovateuk.ifs.registration.service.AcceptProjectInviteController.*;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
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
        setupCookieUtil();
    }

    @Test
    public void testFailureUserExistsButIsLoggedInWithTheWrongUser() throws Exception {
        loggedInUser = newUserResource().withEmail("loggedInUser@example.com").build();
        UserResource inviteUser = newUserResource().withEmail("inviteUser@example.com").build();
        InviteProjectResource invite = newInviteProjectResource().withHash("hash").withStatus(SENT).withEmail(inviteUser.getEmail()).build();
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
        InviteProjectResource invite = newInviteProjectResource().withHash("hash").withStatus(OPENED).withEmail(inviteUser.getEmail()).build();
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
        InviteProjectResource invite = newInviteProjectResource().withHash("hash").withStatus(SENT).withEmail(user.getEmail()).build();
        when(projectInviteRestServiceMock.getInviteByHash(invite.getHash())).thenReturn(restSuccess(invite));
        when(projectInviteRestServiceMock.checkExistingUser(invite.getHash())).thenReturn(restSuccess(true));
        MvcResult result = mockMvc.perform(get(ACCEPT_INVITE_MAPPING + invite.getHash()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(cookie().exists(AcceptProjectInviteController.INVITE_HASH))
                .andExpect(view().name(ACCEPT_INVITE_USER_EXISTS_BUT_NOT_LOGGED_IN_VIEW))
                .andReturn();

        assertEquals(invite.getHash(), getDecryptedCookieValue(result.getResponse().getCookies(), AcceptProjectInviteController.INVITE_HASH));
    }


    @Test
    public void testInviteEntryUserExistsAndIsLoggedIn() throws Exception {
        UserResource inviteUser = newUserResource().withEmail("inviteUser@example.com").build();
        loggedInUser = inviteUser;
        InviteProjectResource invite = newInviteProjectResource().withHash("hash").withStatus(SENT).withEmail(inviteUser.getEmail()).build();
        when(projectInviteRestServiceMock.getInviteByHash(invite.getHash())).thenReturn(restSuccess(invite));
        when(projectInviteRestServiceMock.checkExistingUser(invite.getHash())).thenReturn(restSuccess(true));
        MvcResult result = mockMvc.perform(get(ACCEPT_INVITE_MAPPING + invite.getHash()))
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists(AcceptProjectInviteController.INVITE_HASH))
                .andExpect(redirectedUrl(ACCEPT_INVITE_USER_EXIST_SHOW_PROJECT_MAPPING))
                .andReturn();

        assertEquals(invite.getHash(), getDecryptedCookieValue(result.getResponse().getCookies(), AcceptProjectInviteController.INVITE_HASH));
    }

    @Test
    public void testInviteEntryUserDoesNotExistAndNotLoggedIn() throws Exception {
        loggedInUser = null;
        InviteProjectResource invite = newInviteProjectResource().withHash("hash").withStatus(SENT).withEmail("doesNotYetExist@example.com").build();
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
        InviteProjectResource invite = newInviteProjectResource().withHash("hash").withStatus(SENT).withEmail("doesNotYetExist@example.com").withProjectName("project name").withOrganisation(organisation.getId()).build();
        when(projectInviteRestServiceMock.getInviteByHash(invite.getHash())).thenReturn(restSuccess(invite));
        when(projectInviteRestServiceMock.checkExistingUser(invite.getHash())).thenReturn(restSuccess(false));
        when(organisationRestService.getOrganisationByIdForAnonymousUserFlow(invite.getOrganisation())).thenReturn(restSuccess(organisation));
        when(organisationRestService.getOrganisationById(anyLong())).thenReturn(restSuccess(organisation));
        MvcResult mvcResult = mockMvc.perform(get(ACCEPT_INVITE_USER_DOES_NOT_YET_EXIST_SHOW_PROJECT_MAPPING).cookie(new Cookie(AcceptProjectInviteController.INVITE_HASH, encryptor.encrypt(invite.getHash()))))
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
        InviteProjectResource invite = newInviteProjectResource().
                withHash("hash").
                withStatus(SENT).
                withEmail(inviteUser.getEmail()).
                withProjectName("project name").
                withOrganisation(organisation.getId()).
                build();
        when(projectInviteRestServiceMock.getInviteByHash(invite.getHash())).thenReturn(restSuccess(invite));
        when(projectInviteRestServiceMock.checkExistingUser(invite.getHash())).thenReturn(restSuccess(true));
        when(organisationRestService.getOrganisationByIdForAnonymousUserFlow(invite.getOrganisation())).thenReturn(restSuccess(organisation));
        when(organisationRestService.getOrganisationById(anyLong())).thenReturn(restSuccess(organisation));
        MvcResult mvcResult = mockMvc.perform(get(ACCEPT_INVITE_USER_EXIST_SHOW_PROJECT_MAPPING).cookie(new Cookie(AcceptProjectInviteController.INVITE_HASH, encryptor.encrypt(invite.getHash()))))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name(ACCEPT_INVITE_SHOW_PROJECT)).andReturn();
        JoinAProjectViewModel model = (JoinAProjectViewModel)mvcResult.getModelAndView().getModel().get("model");
        Boolean userExists = (Boolean)mvcResult.getModelAndView().getModel().get("userExists");
        assertEquals(model.getProjectName(), invite.getProjectName());
        assertTrue(model.getProjectName(), userExists);
    }
}