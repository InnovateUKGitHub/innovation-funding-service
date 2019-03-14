package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.service.MonitoringOfficerRegistrationRestService;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.MonitoringOfficerInviteResource;
import org.innovateuk.ifs.registration.form.MonitoringOfficerRegistrationForm;
import org.innovateuk.ifs.registration.populator.MonitoringOfficerRegistrationModelPopulator;
import org.innovateuk.ifs.registration.service.MonitoringOfficerService;
import org.innovateuk.ifs.registration.viewmodel.MonitoringOfficerRegistrationViewModel;
import org.innovateuk.ifs.util.NavigationUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
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
    private MonitoringOfficerRegistrationRestService competitionSetupMonitoringOfficerRestServiceMock;

    @Mock
    private MonitoringOfficerService monitoringOfficerServiceMock;

    @Mock
    private NavigationUtils navigationUtilsMock;

    @Override
    protected MonitoringOfficerRegistrationController supplyControllerUnderTest() {
        return new MonitoringOfficerRegistrationController(monitoringOfficerRegistrationModelPopulatorMock,
                competitionSetupMonitoringOfficerRestServiceMock, monitoringOfficerServiceMock, navigationUtilsMock);
    }

    @Test
    public void openInvite() throws Exception {
        setLoggedInUser(null);

        boolean existingUser = false;
        String hash = "hash";
        MonitoringOfficerInviteResource monitoringOfficerInviteResource = newMonitoringOfficerInviteResource()
                .withEmail("tom@poly.io")
                .build();
        MonitoringOfficerRegistrationViewModel expectedViewModel = new MonitoringOfficerRegistrationViewModel(monitoringOfficerInviteResource.getEmail());

        when(competitionSetupMonitoringOfficerRestServiceMock.checkExistingUser(hash)).thenReturn(restSuccess(existingUser));
        when(competitionSetupMonitoringOfficerRestServiceMock.openMonitoringOfficerInvite(hash)).thenReturn(restSuccess(monitoringOfficerInviteResource));
        when(monitoringOfficerRegistrationModelPopulatorMock.populateModel(monitoringOfficerInviteResource.getEmail())).thenReturn(expectedViewModel);

        mockMvc.perform(get(URL_PREFIX + "/{hash}/register", hash))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("monitoring-officer/create-account"));

        InOrder inOrder = inOrder(competitionSetupMonitoringOfficerRestServiceMock, monitoringOfficerRegistrationModelPopulatorMock);
        inOrder.verify(competitionSetupMonitoringOfficerRestServiceMock).checkExistingUser(hash);
        inOrder.verify(competitionSetupMonitoringOfficerRestServiceMock).openMonitoringOfficerInvite(hash);
        inOrder.verify(monitoringOfficerRegistrationModelPopulatorMock).populateModel(monitoringOfficerInviteResource.getEmail());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void openInvite_existingUser() throws Exception {
        setLoggedInUser(null);

        boolean existingUser = true;
        String hash = "hash";
        MonitoringOfficerInviteResource monitoringOfficerInviteResource = newMonitoringOfficerInviteResource()
                .withEmail("tom@poly.io")
                .build();
        String redirectUrl = "/to/dashboard/";

        when(competitionSetupMonitoringOfficerRestServiceMock.checkExistingUser(hash)).thenReturn(restSuccess(existingUser));
        when(competitionSetupMonitoringOfficerRestServiceMock.addMonitoringOfficerRole(hash)).thenReturn(restSuccess());
        when(navigationUtilsMock.getRedirectToLandingPageUrl(any(HttpServletRequest.class))).thenReturn("redirect:" + redirectUrl);

        mockMvc.perform(get(URL_PREFIX + "/{hash}/register", hash))
                .andExpect(model().hasNoErrors())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(redirectUrl));

        InOrder inOrder = inOrder(competitionSetupMonitoringOfficerRestServiceMock, monitoringOfficerRegistrationModelPopulatorMock, navigationUtilsMock);
        inOrder.verify(competitionSetupMonitoringOfficerRestServiceMock).checkExistingUser(hash);
        inOrder.verify(competitionSetupMonitoringOfficerRestServiceMock).addMonitoringOfficerRole(hash);
        inOrder.verify(navigationUtilsMock).getRedirectToLandingPageUrl(any(HttpServletRequest.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void openInvite_existingUserLoggedIn() throws Exception {
        boolean existingUser = true;
        String hash = "hash";
        MonitoringOfficerInviteResource monitoringOfficerInviteResource = newMonitoringOfficerInviteResource()
                .withEmail(getLoggedInUser().getEmail())
                .build();
        String redirectUrl = "/to/dashboard/";

        when(competitionSetupMonitoringOfficerRestServiceMock.getMonitoringOfficerInvite(hash)).thenReturn(restSuccess(monitoringOfficerInviteResource));
        when(competitionSetupMonitoringOfficerRestServiceMock.checkExistingUser(hash)).thenReturn(restSuccess(existingUser));
        when(competitionSetupMonitoringOfficerRestServiceMock.addMonitoringOfficerRole(hash)).thenReturn(restSuccess());
        when(navigationUtilsMock.getRedirectToLandingPageUrl(any(HttpServletRequest.class))).thenReturn("redirect:" + redirectUrl);

        mockMvc.perform(get(URL_PREFIX + "/{hash}/register", hash))
                .andExpect(model().hasNoErrors())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(redirectUrl));

        InOrder inOrder = inOrder(competitionSetupMonitoringOfficerRestServiceMock, monitoringOfficerRegistrationModelPopulatorMock, navigationUtilsMock);
        inOrder.verify(competitionSetupMonitoringOfficerRestServiceMock).checkExistingUser(hash);
        inOrder.verify(competitionSetupMonitoringOfficerRestServiceMock).addMonitoringOfficerRole(hash);
        inOrder.verify(navigationUtilsMock).getRedirectToLandingPageUrl(any(HttpServletRequest.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void openInvite_loggedInOtherUser() throws Exception {
        String hash = "hash";
        MonitoringOfficerInviteResource monitoringOfficerInviteResource = newMonitoringOfficerInviteResource()
                .withEmail("tom@poly.io")
                .build();

        when(competitionSetupMonitoringOfficerRestServiceMock.getMonitoringOfficerInvite(hash)).thenReturn(restSuccess(monitoringOfficerInviteResource));

        mockMvc.perform(get(URL_PREFIX + "/{hash}/register", hash))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("registration/error"));

        verify(competitionSetupMonitoringOfficerRestServiceMock, only()).getMonitoringOfficerInvite(hash);
    }

    @Test
    public void openInvite_notFound() throws Exception {
        setLoggedInUser(null);

        String hash = "hash";
        MonitoringOfficerInviteResource monitoringOfficerInviteResource = newMonitoringOfficerInviteResource()
                .withEmail("tom@poly.io")
                .build();
        MonitoringOfficerRegistrationViewModel expectedViewModel = new MonitoringOfficerRegistrationViewModel(monitoringOfficerInviteResource.getEmail());

        when(competitionSetupMonitoringOfficerRestServiceMock.checkExistingUser(hash))
                .thenReturn(restFailure(notFoundError(MonitoringOfficerInviteResource.class)));

        mockMvc.perform(get(URL_PREFIX + "/{hash}/register", hash))
                .andExpect(status().isNotFound());

        verify(competitionSetupMonitoringOfficerRestServiceMock, only()).checkExistingUser(hash);
    }

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
                .thenReturn(restSuccess(inviteResource));

        mockMvc.perform(get(URL_PREFIX + "/{hash}/register/account-created", hash))
                .andExpect(status().isOk())
                .andExpect(view().name("registration/account-created"));

        verify(competitionSetupMonitoringOfficerRestServiceMock, only()).getMonitoringOfficerInvite(hash);
    }
}
