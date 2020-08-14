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

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.invite.builder.RoleInviteResourceBuilder.newRoleInviteResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ExternalUserRegistrationControllerTest extends BaseControllerMockMVCTest<ExternalUserRegistrationController> {

    private static final String URL_PREFIX = "/registration";

    @Mock
    private InviteUserRestService inviteUserRestService;

    @Mock
    private UserRestService userRestService;

    @Override
    protected ExternalUserRegistrationController supplyControllerUnderTest() {
        return new ExternalUserRegistrationController();
    }

    @Test
    public void testYourDetails() throws Exception {
        setLoggedInUser(null);


        when(inviteUserRestService.getInvite("hash")).thenReturn(restSuccess(newRoleInviteResource()
                .withRole(Role.KNOWLEDGE_TRANSFER_ADVISOR)
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
    }

    @Test
    public void testSubmitYourDetails() throws Exception {
        setLoggedInUser(null);
        when(inviteUserRestService.getInvite("hash")).thenReturn(restSuccess(newRoleInviteResource()
                .withRole(Role.KNOWLEDGE_TRANSFER_ADVISOR)
                .withEmail("blah@gmail.com")
                .build()));

        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.setEmail("blah@gmail.com");
        registrationForm.setFirstName("Bob");
        registrationForm.setLastName("Person");
        registrationForm.setPassword("password");
        registrationForm.setPhoneNumber("123123123123");
        registrationForm.setTermsAndConditions("1");
        when(userRestService.createUser(refEq(registrationForm.constructUserCreationResource()
                .withInviteHash("hash")
                .withRole(Role.KNOWLEDGE_TRANSFER_ADVISOR)
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
    public void testAccountCreated() throws Exception {
        setLoggedInUser(null);
        when(inviteUserRestService.checkExistingUser("hash")).thenReturn(RestResult.restSuccess(true));
        mockMvc.perform(get(URL_PREFIX + "/hash/register/account-created"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration/external-account-created"));
    }
}
