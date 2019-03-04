package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.service.CompetitionSetupStakeholderRestService;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.StakeholderInviteResource;
import org.innovateuk.ifs.registration.form.StakeholderRegistrationForm;
import org.innovateuk.ifs.registration.populator.StakeholderRegistrationModelPopulator;
import org.innovateuk.ifs.registration.service.StakeholderService;
import org.innovateuk.ifs.registration.viewmodel.StakeholderRegistrationViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;

import static org.innovateuk.ifs.stakeholder.builder.StakeholderInviteResourceBuilder.newStakeholderInviteResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.Silent.class)
public class StakeholderRegistrationControllerTest extends BaseControllerMockMVCTest<StakeholderRegistrationController> {

    private static final String URL_PREFIX = "/stakeholder";

    @Mock
    private StakeholderRegistrationModelPopulator stakeholderRegistrationModelPopulatorMock;

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

        when(stakeholderRegistrationModelPopulatorMock.populateModel("tyler@hiveit.co.uk")).thenReturn(new StakeholderRegistrationViewModel("test@test.com", "Stakeholder"));
        when(competitionSetupStakeholderRestServiceMock.getStakeholderInvite("hash")).thenReturn(RestResult.restSuccess(inviteResource));
        mockMvc.perform(get(URL_PREFIX + "/hash/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("stakeholders/create-account"));
    }

    @Test
    public void submitYourDetails() throws Exception {
        setLoggedInUser(null);
        StakeholderRegistrationForm registrationForm = new StakeholderRegistrationForm("Tyler", "Newall", "Passw0rd");

        StakeholderInviteResource inviteResource = newStakeholderInviteResource()
                .withStatus(InviteStatus.SENT)
                .build();

        when(stakeholderServiceMock.createStakeholder("hash", registrationForm)).thenReturn(ServiceResult.serviceSuccess());
        when(competitionSetupStakeholderRestServiceMock.getStakeholderInvite("hash")).thenReturn(RestResult.restSuccess(inviteResource));
        mockMvc.perform(post(URL_PREFIX + "/hash/register")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("firstName", registrationForm.getFirstName())
            .param("lastName", registrationForm.getLastName())
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
