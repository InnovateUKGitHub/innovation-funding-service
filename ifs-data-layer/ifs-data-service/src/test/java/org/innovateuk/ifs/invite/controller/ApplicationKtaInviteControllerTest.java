package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;
import org.innovateuk.ifs.invite.transactional.ApplicationKtaInviteService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.ApplicationKtaInviteResourceBuilder.newApplicationKtaInviteResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationKtaInviteControllerTest extends BaseControllerMockMVCTest<ApplicationKtaInviteController> {

    @Mock
    private ApplicationKtaInviteService applicationKtaInviteService;

    @Before
    public void setUp() {
       // when(inviteOrganisationRepositoryMock.save(isA(InviteOrganisation.class))).thenReturn(null);
     //   when(applicationInviteRepositoryMock.save(isA(ApplicationInvite.class))).thenReturn(null);
     //   when(organisationRepositoryMock.findById(1L)).thenReturn(Optional.of(newOrganisation().build()));
      //  when(applicationRepositoryMock.findById(1L)).thenReturn(Optional.of(newApplication().build()));
    }

    @Override
    protected ApplicationKtaInviteController supplyControllerUnderTest() {
        return new ApplicationKtaInviteController();
    }

    @Test
    public void resendKtaInvite() throws Exception {

        long applicationId = 1L;
        ApplicationKtaInviteResource inviteResource = newApplicationKtaInviteResource()
                .withApplication(applicationId)
                .withEmail("testemail")
                .build();

        when(applicationKtaInviteService.resendKtaInvite(inviteResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/kta-invite/resend-kta-invite")
                .contentType(APPLICATION_JSON)
                .content(toJson(inviteResource)))
                .andExpect(status().isCreated());
    }

    @Test
    public void saveKtaInvite() throws Exception {

        long applicationId = 1L;
        ApplicationKtaInviteResource inviteResource = newApplicationKtaInviteResource()
                .withApplication(applicationId)
                .withEmail("testemail")
                .build();

        when(applicationKtaInviteService.saveKtaInvite(inviteResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/kta-invite/save-kta-invite")
                .contentType(APPLICATION_JSON)
                .content(toJson(inviteResource)))
                .andExpect(status().isCreated());
    }

    @Test
    public void removeKtaInvite() throws Exception {

        long applicationInviteId = 456L;
        when(applicationKtaInviteService.removeKtaApplicationInvite(applicationInviteId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/kta-invite/remove-kta-invite/"+applicationInviteId))
                .andExpect(status().isNoContent());
    }
}
