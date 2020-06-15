package org.innovateuk.ifs.registration.grantsinvite.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.grants.service.GrantsInviteRestService;
import org.innovateuk.ifs.grantsinvite.resource.SentGrantsInviteResource;
import org.innovateuk.ifs.registration.grantsinvite.viewmodel.GrantsInviteViewModel;
import org.innovateuk.ifs.util.EncryptedCookieService;
import org.innovateuk.ifs.util.NavigationUtils;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.grantsinvite.builder.SentGrantsInviteResourceBuilder.newSentGrantsInviteResource;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class AcceptGrantsInviteControllerTest extends BaseControllerMockMVCTest<AcceptGrantsInviteController> {

//    @RequestMapping("/project/{projectId}/grants/invite")

    @Mock
    private GrantsInviteRestService grantsInviteRestService;

    @Mock
    private EncryptedCookieService cookieUtil;

    @Mock
    private NavigationUtils navigationUtils;

    @Test
    public void inviteEntryPage() throws Exception {
        String hash = "hash";
        long projectId = 1L;
        SentGrantsInviteResource invite = newSentGrantsInviteResource()
                .withStatus(SENT)
                .withEmail("plonker@gmail.com")
                .build();

        when(grantsInviteRestService.getInviteByHash(projectId, hash)).thenReturn(restSuccess(invite));

        MvcResult result = mockMvc.perform(get("/project/{id}/grants/invite/{hash}", projectId, hash))
                .andExpect(status().isOk())
                .andExpect(view().name("project/registration/accept-invite-failure"))
                .andReturn();

        GrantsInviteViewModel viewmodel = (GrantsInviteViewModel) result.getModelAndView().getModel().get("model");

        assertFalse(viewmodel.isUserExists());
        assertEquals(invite.getProjectName(), viewmodel.getProjectName());
    }

    @Override
    protected AcceptGrantsInviteController supplyControllerUnderTest() {
        return new AcceptGrantsInviteController();
    }
}
