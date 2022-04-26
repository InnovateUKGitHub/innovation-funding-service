package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.service.InviteUserRestService;
import org.innovateuk.ifs.registration.form.RegistrationForm;
import org.innovateuk.ifs.registration.viewmodel.RegistrationViewModel;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import javax.validation.Validator;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.invite.builder.RoleInviteResourceBuilder.newRoleInviteResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ExternalUserRegistrationControllerTest extends BaseControllerMockMVCTest<ExternalUserRegistrationController> {

    private static final String URL_PREFIX = "/registration";

    @Mock
    private InviteUserRestService inviteUserRestService;

    @Mock
    private UserRestService userRestService;

    @Mock
    private Validator validator;

    @Override
    protected ExternalUserRegistrationController supplyControllerUnderTest() {
        return new ExternalUserRegistrationController();
    }

    @Test
    public void yourDetails() throws Exception {
        setLoggedInUser(null);


        when(inviteUserRestService.getInvite("hash")).thenReturn(restSuccess(newRoleInviteResource()
                .withRole(Role.KNOWLEDGE_TRANSFER_ADVISER)
                .withEmail("blah@gmail.com")
                .build()));
        MvcResult result = mockMvc.perform(get(URL_PREFIX + "/hash/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration/register"))
                .andReturn();

        RegistrationForm form = (RegistrationForm) result.getModelAndView().getModel().get("form");
        assertEquals(form.getEmail(), "blah@gmail.com");

        RegistrationViewModel viewModel = (RegistrationViewModel) result.getModelAndView().getModel().get("model");
        assertTrue(viewModel.isPhoneRequired());
        assertTrue(viewModel.isTermsRequired());
        assertTrue(viewModel.isAddressRequired());
        assertFalse(viewModel.isShowBackLink());
    }

    @Test
    public void submitYourDetails() throws Exception {
        setLoggedInUser(null);
        when(inviteUserRestService.getInvite("hash")).thenReturn(restSuccess(newRoleInviteResource()
                .withRole(Role.KNOWLEDGE_TRANSFER_ADVISER)
                .withEmail("blah@gmail.com")
                .build()));

        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.setEmail("blah@gmail.com");
        registrationForm.setFirstName("Bob");
        registrationForm.setLastName("Person");
        registrationForm.setPassword("password1357");
        registrationForm.setPhoneNumber("123123123123");
        registrationForm.setTermsAndConditions("1");
        when(userRestService.createUser(refEq(registrationForm.constructUserCreationResource()
                .withInviteHash("hash")
                .withRole(Role.KNOWLEDGE_TRANSFER_ADVISER)
                .build())))
            .thenReturn(restSuccess(new UserResource()));
        mockMvc.perform(post(URL_PREFIX + "/hash/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("firstName", registrationForm.getFirstName())
                .param("lastName", registrationForm.getLastName())
                .param("password", registrationForm.getPassword())
                .param("email", registrationForm.getEmail())
                .param("phoneNumber", registrationForm.getPhoneNumber())
                .param("termsAndConditions", registrationForm.getTermsAndConditions()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registration/hash/register/account-created"));
    }

    @Test
    public void accountCreated() throws Exception {
        setLoggedInUser(null);
        when(inviteUserRestService.checkExistingUser("hash")).thenReturn(RestResult.restSuccess(true));
        mockMvc.perform(get(URL_PREFIX + "/hash/register/account-created"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration/external-account-created"));
    }

    @Test
    public void yourDetailsAssessor() throws Exception {
        setLoggedInUser(null);


        when(inviteUserRestService.getInvite("hash")).thenReturn(restSuccess(newRoleInviteResource()
                .withRole(Role.ASSESSOR)
                .withEmail("newAssessor@gmail.com")
                .build()));
        MvcResult result = mockMvc.perform(get(URL_PREFIX + "/hash/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration/register"))
                .andReturn();

        RegistrationForm form = (RegistrationForm) result.getModelAndView().getModel().get("form");
        assertEquals(form.getEmail(), "newAssessor@gmail.com");

        RegistrationViewModel viewModel = (RegistrationViewModel) result.getModelAndView().getModel().get("model");
        assertTrue(viewModel.isPhoneRequired());
        assertTrue(viewModel.isAddressRequired());
        assertFalse(viewModel.isShowBackLink());
        assertFalse(viewModel.isTermsRequired());
        assertEquals("Continue", viewModel.getButtonText());
    }

    @Test
    public void submitYourDetailsAssessor() throws Exception {
        setLoggedInUser(null);
        when(inviteUserRestService.getInvite("hash")).thenReturn(restSuccess(newRoleInviteResource()
                .withRole(Role.ASSESSOR)
                .withEmail("newAssessor@gmail.com")
                .build()));

        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.setEmail("newAssessor@gmail.com");
        registrationForm.setFirstName("Bob");
        registrationForm.setLastName("Person");
        registrationForm.setPassword("password1357");
        registrationForm.setPhoneNumber("123123123123");
        when(userRestService.createUser(refEq(registrationForm.constructUserCreationResource()
                .withInviteHash("hash")
                .withRole(Role.ASSESSOR)
                .build())))
                .thenReturn(restSuccess(newUserResource().withId(1L).withRoleGlobal(Role.ASSESSOR).build()));
        mockMvc.perform(post(URL_PREFIX + "/hash/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", registrationForm.getFirstName())
                        .param("lastName", registrationForm.getLastName())
                        .param("password", registrationForm.getPassword())
                        .param("email", registrationForm.getEmail())
                        .param("phoneNumber", registrationForm.getPhoneNumber()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registration/hash/register/account-created"));   // TODO change expectedRedirectUrl in ExternalUserRegistrationController.submitYourDetails, IFS-11788
    }
}
