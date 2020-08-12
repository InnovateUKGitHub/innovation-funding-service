package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;
import org.innovateuk.ifs.invite.transactional.ApplicationKtaInviteService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.ApplicationKtaInviteResourceBuilder.newApplicationKtaInviteResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationKtaInviteControllerTest extends BaseControllerMockMVCTest<ApplicationKtaInviteController> {

    @Mock
    private ApplicationKtaInviteService applicationKtaInviteService;

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

        long applicationId = 456L;
        when(applicationKtaInviteService.removeKtaInviteByApplication(applicationId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/kta-invite/remove-kta-invite-by-application/"+applicationId))
                .andExpect(status().isNoContent());
    }
}
