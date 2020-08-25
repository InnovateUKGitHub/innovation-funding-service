package org.innovateuk.ifs.management.registration.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.service.InviteUserRestService;
import org.innovateuk.ifs.management.registration.service.InternalUserService;
import org.innovateuk.ifs.registration.form.RegistrationForm;
import org.innovateuk.ifs.registration.viewmodel.RegistrationViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.RoleInviteResourceBuilder.newRoleInviteResource;
import static org.innovateuk.ifs.registration.viewmodel.RegistrationViewModel.RegistrationViewModelBuilder.aRegistrationViewModel;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class InternalUserRegistrationControllerTest  extends BaseControllerMockMVCTest<InternalUserRegistrationController> {

    private static final String URL_PREFIX = "/registration";

    @Mock
    private InternalUserService internalUserServiceMock;

    @Mock
    private InviteUserRestService inviteUserRestServiceMock;

    @Override
    protected InternalUserRegistrationController supplyControllerUnderTest() {
        return new InternalUserRegistrationController();
    }

    @Test
    public void testYourDetails() throws Exception {
        setLoggedInUser(null);

        when(inviteUserRestServiceMock.getInvite("hash")).thenReturn(restSuccess(newRoleInviteResource().build()));
        RegistrationViewModel viewModel = aRegistrationViewModel().withPhoneRequired(false).withTermsRequired(false).withInvitee(true).build();
        mockMvc.perform(get(URL_PREFIX + "/hash/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration/register"))
                .andExpect(model().attribute("model", samePropertyValuesAs(viewModel)));
    }

    @Test
    public void testSubmitYourDetails() throws Exception {
        setLoggedInUser(null);
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.setFirstName("Arden");
        registrationForm.setLastName("Pimenta");
        registrationForm.setPassword("Passw0rd");
        registrationForm.setEmail("arden.piment@innovateuk.test");

        when(internalUserServiceMock.createInternalUser(eq("hash"), refEq(registrationForm))).thenReturn(serviceSuccess());
        mockMvc.perform(post(URL_PREFIX + "/hash/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("firstName", registrationForm.getFirstName())
                .param("lastName", registrationForm.getLastName())
                .param("password", registrationForm.getPassword())
                .param("email", registrationForm.getEmail()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registration/hash/register/account-created"));
    }

    @Test
    public void testAccountCreated() throws Exception {
        setLoggedInUser(null);
        when(inviteUserRestServiceMock.checkExistingUser("hash")).thenReturn(RestResult.restSuccess(true));
        mockMvc.perform(get(URL_PREFIX + "/hash/register/account-created"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration/account-created"));
    }
}
