package org.innovateuk.ifs.project.monitoring.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.crm.transactional.CrmService;
import org.innovateuk.ifs.invite.resource.MonitoringOfficerCreateResource;
import org.innovateuk.ifs.invite.resource.MonitoringOfficerInviteResource;
import org.innovateuk.ifs.project.monitoring.transactional.MonitoringOfficerInviteService;
import org.innovateuk.ifs.registration.resource.MonitoringOfficerRegistrationResource;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.RegistrationService;
import org.innovateuk.ifs.user.transactional.UserService;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.MonitoringOfficerInviteResourceBuilder.newMonitoringOfficerInviteResource;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MonitoringOfficerInviteControllerTest extends BaseControllerMockMVCTest<MonitoringOfficerInviteController> {

    @Mock
    private RegistrationService registrationServiceMock;

    @Mock
    private MonitoringOfficerInviteService monitoringOfficerInviteServiceMock;

    @Mock
    private CrmService crmServiceMock;

    @Mock
    private UserService userServiceMock;

    @Override
    protected MonitoringOfficerInviteController supplyControllerUnderTest() {
        return new MonitoringOfficerInviteController(monitoringOfficerInviteServiceMock,
                registrationServiceMock,
                crmServiceMock,
                userServiceMock);
    }

    @Test
    public void openInvite() throws Exception {
        String hash = "hash";
        MonitoringOfficerInviteResource invite = newMonitoringOfficerInviteResource()
                .withHash(hash)
                .build();

        when(monitoringOfficerInviteServiceMock.openInvite(hash)).thenReturn(serviceSuccess(invite));

        mockMvc.perform(get("/monitoring-officer-registration/open-monitoring-officer-invite/{hash}", hash))
                .andExpect(status().isOk());

        verify(monitoringOfficerInviteServiceMock, only()).openInvite(hash);
    }

    @Test
    public void getInvite() throws Exception {
        String hash = "hash";
        MonitoringOfficerInviteResource invite = newMonitoringOfficerInviteResource()
                .withHash(hash)
                .build();

        when(monitoringOfficerInviteServiceMock.getInviteByHash(hash)).thenReturn(serviceSuccess(invite));

        mockMvc.perform(get("/monitoring-officer-registration/get-monitoring-officer-invite/{hash}", hash))
                .andExpect(status().isOk());

        verify(monitoringOfficerInviteServiceMock, only()).getInviteByHash(hash);
    }

    @Test
    public void createMonitoringOfficer() throws Exception {
        String hash = "hash";
        MonitoringOfficerRegistrationResource registrationResource = new MonitoringOfficerRegistrationResource(
                "tom", "baldwin", "0114 286 6356", "password"
        );
        User user = newUser().build();

        when(monitoringOfficerInviteServiceMock.activateUserByHash(anyString(), any())).thenReturn(serviceSuccess(user));
        when(crmServiceMock.syncCrmContact(user.getId())).thenReturn(serviceSuccess());

        mockMvc.perform(post("/monitoring-officer-registration/monitoring-officer/create/{hash}", hash)
                .contentType(APPLICATION_JSON)
                .content(toJson(registrationResource)))
                .andExpect(status().is2xxSuccessful());

        InOrder inOrder = inOrder(monitoringOfficerInviteServiceMock, crmServiceMock);

        inOrder.verify(monitoringOfficerInviteServiceMock).activateUserByHash(hash, registrationResource);
        inOrder.verify(crmServiceMock).syncCrmContact(user.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void checkExistingUser() throws Exception {
        String hash = "hash";

        when(monitoringOfficerInviteServiceMock.checkUserExistsForInvite(hash)).thenReturn(serviceSuccess(true));

        mockMvc.perform(get("/monitoring-officer-registration/monitoring-officer/check-existing-user/{hash}", hash))
                .andExpect(status().isOk());

        verify(monitoringOfficerInviteServiceMock, only()).checkUserExistsForInvite(hash);
    }

    @Test
    public void addMonitoringOfficerRole() throws Exception {
        String hash = "hash";

        User user = newUser().build();

        when(monitoringOfficerInviteServiceMock.addMonitoringOfficerRole(hash)).thenReturn(serviceSuccess(user));
        when(crmServiceMock.syncCrmContact(user.getId())).thenReturn(serviceSuccess());

        mockMvc.perform(post("/monitoring-officer-registration/monitoring-officer/add-monitoring-officer-role/{hash}", hash))
                .andExpect(status().is2xxSuccessful());

        InOrder inOrder = inOrder(monitoringOfficerInviteServiceMock, crmServiceMock);

        inOrder.verify(monitoringOfficerInviteServiceMock).addMonitoringOfficerRole(hash);
        inOrder.verify(crmServiceMock).syncCrmContact(user.getId());
        inOrder.verifyNoMoreInteractions();
    }


    @Test
    public void createPendingMonitoringOfficer() throws Exception {
        UserResource user = newUserResource()
                .withFirstName("Steve")
                .withLastName("Smith")
                .withEmail("steve@smith.com")
                .withPhoneNumber("01142356565")
                .build();

        MonitoringOfficerCreateResource resource = new MonitoringOfficerCreateResource(
                user.getFirstName(), user.getLastName(), user.getPhoneNumber(), user.getEmail());

        when(userServiceMock.findByEmail(user.getEmail()))
                .thenReturn(serviceFailure(notFoundError(User.class, user.getEmail())));

        when(registrationServiceMock.createUser(any()))
                .thenReturn(serviceSuccess(user));

        mockMvc.perform(post("/monitoring-officer-registration/create-pending-monitoring-officer")
                .content(toJson(resource))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(userServiceMock).findByEmail(user.getEmail());
        verify(registrationServiceMock).createUser(any());
    }
}