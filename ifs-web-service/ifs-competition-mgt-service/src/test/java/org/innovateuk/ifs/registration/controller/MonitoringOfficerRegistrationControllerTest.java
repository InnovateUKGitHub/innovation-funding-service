package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.service.CompetitionSetupMonitoringOfficerRestService;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.MonitoringOfficerInviteResource;
import org.innovateuk.ifs.registration.form.MonitoringOfficerRegistrationForm;
import org.innovateuk.ifs.registration.populator.MonitoringOfficerRegistrationModelPopulator;
import org.innovateuk.ifs.registration.service.MonitoringOfficerService;
import org.innovateuk.ifs.registration.viewmodel.MonitoringOfficerRegistrationViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;

import static org.innovateuk.ifs.invite.builder.MonitoringOfficerInviteResourceBuilder.newMonitoringOfficerInviteResource;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class MonitoringOfficerRegistrationControllerTest extends BaseControllerMockMVCTest<MonitoringOfficerRegistrationController> {

    private static final String URL_PREFIX = "/monitoring-officer";

    @Mock
    private MonitoringOfficerRegistrationModelPopulator monitoringOfficerRegistrationModelPopulatorMock;

    @Mock
    private CompetitionSetupMonitoringOfficerRestService competitionSetupMonitoringOfficerRestServiceMock;

    @Mock
    private MonitoringOfficerService monitoringOfficerServiceMock;


    @Override
    protected MonitoringOfficerRegistrationController supplyControllerUnderTest() {
        return new MonitoringOfficerRegistrationController(monitoringOfficerRegistrationModelPopulatorMock,
                competitionSetupMonitoringOfficerRestServiceMock, monitoringOfficerServiceMock);
    }

    @Test
    public void createAccount() throws Exception {
        String hash = "hash";
        MonitoringOfficerInviteResource monitoringOfficerInviteResource = newMonitoringOfficerInviteResource()
                .withEmail("tom@tombaldwin.net")
                .build();
        MonitoringOfficerRegistrationViewModel expectedViewModel = new MonitoringOfficerRegistrationViewModel(monitoringOfficerInviteResource.getEmail());

        when(competitionSetupMonitoringOfficerRestServiceMock.getMonitoringOfficerInvite(hash)).thenReturn(RestResult.restSuccess(monitoringOfficerInviteResource));
        when(monitoringOfficerRegistrationModelPopulatorMock.populateModel(monitoringOfficerInviteResource.getEmail())).thenReturn(expectedViewModel);

        mockMvc.perform(get(URL_PREFIX + "/{hash}/register", hash))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("monitoring-officer/create-account"));


        InOrder inOrder = inOrder(competitionSetupMonitoringOfficerRestServiceMock, monitoringOfficerRegistrationModelPopulatorMock);
        inOrder.verify(competitionSetupMonitoringOfficerRestServiceMock).getMonitoringOfficerInvite(hash);
        inOrder.verify(monitoringOfficerRegistrationModelPopulatorMock).populateModel(monitoringOfficerInviteResource.getEmail());
        inOrder.verifyNoMoreInteractions();
    }


    // TODO create account not found

    @Test
    public void submitDetails() throws Exception {
        setLoggedInUser(null);

        String hash = "hash";
        MonitoringOfficerRegistrationForm registrationForm =
                new MonitoringOfficerRegistrationForm("Tom", "Baldwin", "Passw0rd", "0114 286 6356");

        when(monitoringOfficerServiceMock.createMonitoringOfficer(hash, registrationForm)).thenReturn(ServiceResult.serviceSuccess());

        mockMvc.perform(post(URL_PREFIX + "/{hash}/register", hash)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("firstName", registrationForm.getFirstName())
                .param("lastName", registrationForm.getLastName())
                .param("phoneNumber", registrationForm.getPhoneNumber())
                .param("password", registrationForm.getPassword()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/monitoring-officer/hash/register/account-created"));

        InOrder inOrder = inOrder(monitoringOfficerServiceMock, competitionSetupMonitoringOfficerRestServiceMock);
        inOrder.verify(monitoringOfficerServiceMock).createMonitoringOfficer(hash, registrationForm);
        inOrder.verifyNoMoreInteractions();
    }


    @Test
    public void accountCreated() throws Exception {
        setLoggedInUser(null);

        String hash = "hash";
        MonitoringOfficerInviteResource inviteResource = newMonitoringOfficerInviteResource()
                .withStatus(InviteStatus.OPENED)
                .build();

        when(competitionSetupMonitoringOfficerRestServiceMock.getMonitoringOfficerInvite(hash))
                .thenReturn(RestResult.restSuccess(inviteResource));

        mockMvc.perform(get(URL_PREFIX + "/{hash}/register/account-created", hash))
                .andExpect(status().isOk())
                .andExpect(view().name("registration/account-created"));

        verify(competitionSetupMonitoringOfficerRestServiceMock, only()).getMonitoringOfficerInvite(hash);
    }
}
