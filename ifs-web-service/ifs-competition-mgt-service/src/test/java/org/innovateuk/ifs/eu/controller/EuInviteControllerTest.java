package org.innovateuk.ifs.eu.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.eu.invite.EuInviteRestService;
import org.innovateuk.ifs.eugrant.EuContactPageResource;
import org.innovateuk.ifs.eugrant.EuContactResource;
import org.innovateuk.ifs.eu.viewmodel.EuInviteViewModel;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static com.google.common.primitives.Longs.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class EuInviteControllerTest extends BaseControllerMockMVCTest<EuInviteController> {

    @Mock
    private EuInviteRestService euInviteRestService;

    @Test
    public void notified() throws Exception {

        List<EuContactResource> euContactResources = singletonList(new EuContactResource());
        EuContactPageResource pageResource = new EuContactPageResource();
        pageResource.setContent(euContactResources);

        when(euInviteRestService.getEuGrantsByContactNotified(true, 0, 100))
                .thenReturn(restSuccess(pageResource));

        MvcResult result = mockMvc.perform(get("/eu-invite-notified"))
                .andExpect(status().isOk())
                .andExpect(view().name("eu/notified"))
                .andReturn();

        verify(euInviteRestService).getEuGrantsByContactNotified(true, 0, 100);

        EuInviteViewModel model = (EuInviteViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(euContactResources, model.getContacts());
    }

    @Test
    public void nonNotified() throws Exception {

        List<EuContactResource> euContactResources = singletonList(new EuContactResource());

        EuContactPageResource pageResource = new EuContactPageResource();
        pageResource.setContent(euContactResources);

        when(euInviteRestService.getEuGrantsByContactNotified(false, 0, 100))
                .thenReturn(restSuccess(pageResource));

        MvcResult result = mockMvc.perform(get("/eu-invite-non-notified"))
                .andExpect(status().isOk())
                .andExpect(view().name("eu/non-notified"))
                .andReturn();

        verify(euInviteRestService).getEuGrantsByContactNotified(false, 0, 100);

        EuInviteViewModel model = (EuInviteViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(euContactResources, model.getContacts());
    }

    @Test
    public void sendInvites() throws Exception {
        List<Long> euContactIds = asList(123L, 456L, 789L);

        when(euInviteRestService.sendInvites(euContactIds))
                .thenReturn(restSuccess());

        mockMvc.perform(post("/eu-send-invites")
                                .contentType(APPLICATION_FORM_URLENCODED)
                                .param("euContactIds[0]", "123")
                                .param("euContactIds[1]", "456")
                                .param("euContactIds[2]", "789"))
                .andExpect(status().is3xxRedirection());

        verify(euInviteRestService).sendInvites(euContactIds);
    }

    @Override
    protected EuInviteController supplyControllerUnderTest() {
        return new EuInviteController();
    }
}