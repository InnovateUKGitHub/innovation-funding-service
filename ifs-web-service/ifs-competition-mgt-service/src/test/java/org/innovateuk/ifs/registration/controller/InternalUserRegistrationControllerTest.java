package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.registration.form.InternalUserRegistrationForm;
import org.innovateuk.ifs.registration.populator.InternalUserRegistrationModelPopulator;
import org.innovateuk.ifs.registration.service.InternalUserService;
import org.innovateuk.ifs.registration.viewmodel.InternalUserRegistrationViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class InternalUserRegistrationControllerTest  extends BaseControllerMockMVCTest<InternalUserRegistrationController> {

    private static final String URL_PREFIX = "/registration";

    @Mock
    private InternalUserRegistrationModelPopulator internalUserRegistrationModelPopulatorMock;

    @Mock
    private InternalUserService internalUserServiceMock;

    @Override
    protected InternalUserRegistrationController supplyControllerUnderTest() {
        return new InternalUserRegistrationController();
    }

    @Test
    public void testYourDetails() throws Exception {
        setLoggedInUser(null);

        when(internalUserRegistrationModelPopulatorMock.populateModel("hash")).thenReturn(new InternalUserRegistrationViewModel("Arden Pimenta", "Project Finance", "arden.pimenta@innovateuk.test"));

        mockMvc.perform(get(URL_PREFIX + "/hash/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration/register"));
    }

    @Test
    public void testSubmitYourDetails() throws Exception {
        setLoggedInUser(null);
        InternalUserRegistrationForm registrationForm = new InternalUserRegistrationForm("Arden", "Pimenta", "Passw0rd");
        when(internalUserServiceMock.createInternalUser("hash", registrationForm)).thenReturn(serviceSuccess());
        mockMvc.perform(post(URL_PREFIX + "/hash/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("firstName", registrationForm.getFirstName())
                .param("lastName", registrationForm.getLastName())
                .param("password", registrationForm.getPassword()))
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
