package org.innovateuk.ifs.management.registration.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.service.MonitoringOfficerRegistrationRestService;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.MonitoringOfficerInviteResource;
import org.innovateuk.ifs.management.registration.service.MonitoringOfficerService;
import org.innovateuk.ifs.registration.form.RegistrationForm;
import org.innovateuk.ifs.registration.viewmodel.RegistrationViewModel;
import org.innovateuk.ifs.registration.viewmodel.RegistrationViewModel.RegistrationViewModelBuilder;
import org.innovateuk.ifs.util.NavigationUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.samePropertyValuesAs;
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
    private MonitoringOfficerRegistrationRestService competitionSetupMonitoringOfficerRestServiceMock;

    @Mock
    private MonitoringOfficerService monitoringOfficerServiceMock;

    @Mock
    private NavigationUtils navigationUtilsMock;

    @Override
    protected MonitoringOfficerRegistrationController supplyControllerUnderTest() {
        return new MonitoringOfficerRegistrationController();
    }

    @Test
    public void openInvite() throws Exception {
        setLoggedInUser(null);

        String hash = "hash";
        MonitoringOfficerInviteResource monitoringOfficerInviteResource = newMonitoringOfficerInviteResource()
                .withEmail("tom@poly.io")
                .build();
        RegistrationViewModel expectedViewModel = RegistrationViewModelBuilder.aRegistrationViewModel().withPhoneRequired(true).withTermsRequired(false).withInvitee(true).build();

        when(competitionSetupMonitoringOfficerRestServiceMock.openMonitoringOfficerInvite(hash)).thenReturn(restSuccess(monitoringOfficerInviteResource));

        mockMvc.perform(get(URL_PREFIX + "/{hash}/register", hash))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attribute("model", samePropertyValuesAs(expectedViewModel)))
                .andExpect(view().name("registration/register"));

        verify(competitionSetupMonitoringOfficerRestServiceMock).openMonitoringOfficerInvite(hash);
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

        when(competitionSetupMonitoringOfficerRestServiceMock.openMonitoringOfficerInvite(hash))
                .thenReturn(restFailure(notFoundError(MonitoringOfficerInviteResource.class)));

        mockMvc.perform(get(URL_PREFIX + "/{hash}/register", hash))
                .andExpect(status().isNotFound());

        verify(competitionSetupMonitoringOfficerRestServiceMock, only()).openMonitoringOfficerInvite(hash);
    }

    @Test
    public void submitDetails() throws Exception {
        setLoggedInUser(null);

        String hash = "hash";
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.setFirstName("Tom");
        registrationForm.setLastName("Baldwin");
        registrationForm.setPassword("Passw0rd");
        registrationForm.setEmail("tom.baldwin@gmail.com");
        registrationForm.setPhoneNumber("0114 286 6356");

        when(monitoringOfficerServiceMock.activateAndUpdateMonitoringOfficer(eq(hash), refEq(registrationForm))).thenReturn(ServiceResult.serviceSuccess());

        mockMvc.perform(post(URL_PREFIX + "/{hash}/register", hash)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("firstName", registrationForm.getFirstName())
                .param("lastName", registrationForm.getLastName())
                .param("email", registrationForm.getEmail())
                .param("phoneNumber", registrationForm.getPhoneNumber())
                .param("password", registrationForm.getPassword()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/monitoring-officer/hash/register/account-created"));

        InOrder inOrder = inOrder(monitoringOfficerServiceMock, competitionSetupMonitoringOfficerRestServiceMock);
        inOrder.verify(monitoringOfficerServiceMock).activateAndUpdateMonitoringOfficer(eq(hash), refEq(registrationForm));
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
