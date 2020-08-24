package org.innovateuk.ifs.management.registration.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.service.CompetitionSetupStakeholderRestService;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.StakeholderInviteResource;
import org.innovateuk.ifs.management.registration.service.StakeholderService;
import org.innovateuk.ifs.registration.form.RegistrationForm;
import org.innovateuk.ifs.registration.viewmodel.RegistrationViewModel;
import org.innovateuk.ifs.registration.viewmodel.RegistrationViewModel.RegistrationViewModelBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.innovateuk.ifs.stakeholder.builder.StakeholderInviteResourceBuilder.newStakeholderInviteResource;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class StakeholderRegistrationControllerTest extends BaseControllerMockMVCTest<StakeholderRegistrationController> {

    private static final String URL_PREFIX = "/stakeholder";

    @Mock
    private StakeholderService stakeholderServiceMock;

    @Mock
    private CompetitionSetupStakeholderRestService competitionSetupStakeholderRestServiceMock;


    @Override
    protected StakeholderRegistrationController supplyControllerUnderTest() {
        return new StakeholderRegistrationController();
    }

    @Test
    public void yourDetails() throws Exception {
        setLoggedInUser(null);

        StakeholderInviteResource inviteResource = newStakeholderInviteResource()
                .withStatus(InviteStatus.OPENED)
                .build();
        RegistrationViewModel viewModel =  RegistrationViewModelBuilder.aRegistrationViewModel().withPhoneRequired(false).withTermsRequired(false).withInvitee(true).build();

        when(competitionSetupStakeholderRestServiceMock.getStakeholderInvite("hash")).thenReturn(RestResult.restSuccess(inviteResource));
        mockMvc.perform(get(URL_PREFIX + "/hash/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration/register"))
                .andExpect(model().attribute("model", samePropertyValuesAs(viewModel)));
    }

    @Test
    public void submitYourDetails() throws Exception {
        setLoggedInUser(null);
        RegistrationForm registrationForm = new RegistrationForm("Tyler", "Newall", "Passw0rd");
        registrationForm.setEmail("tyler.newall@gmail.com");

        StakeholderInviteResource inviteResource = newStakeholderInviteResource()
                .withStatus(InviteStatus.SENT)
                .build();

        when(stakeholderServiceMock.createStakeholder(eq("hash"), refEq(registrationForm))).thenReturn(ServiceResult.serviceSuccess());
        when(competitionSetupStakeholderRestServiceMock.getStakeholderInvite("hash")).thenReturn(RestResult.restSuccess(inviteResource));
        mockMvc.perform(post(URL_PREFIX + "/hash/register")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("firstName", registrationForm.getFirstName())
            .param("lastName", registrationForm.getLastName())
            .param("email", registrationForm.getEmail())
            .param("password", registrationForm.getPassword()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/stakeholder/hash/register/account-created"));
    }

    @Test
    public void accountCreated() throws Exception {
        setLoggedInUser(null);
        StakeholderInviteResource inviteResource = newStakeholderInviteResource()
                .withStatus(InviteStatus.OPENED)
                .build();

        when(competitionSetupStakeholderRestServiceMock.getStakeholderInvite("hash")).thenReturn(RestResult.restSuccess(inviteResource));
        mockMvc.perform(get(URL_PREFIX + "/hash/register/account-created"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration/account-created"));
    }
}
