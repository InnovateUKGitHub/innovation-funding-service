package org.innovateuk.ifs.eu.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.eu.invite.EuInviteRestService;
import org.innovateuk.ifs.eu.viewmodel.EuInviteViewModel;
import org.innovateuk.ifs.eugrant.EuGrantPageResource;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
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

        List<EuGrantResource> euGrantResources = singletonList(new EuGrantResource());
        EuGrantPageResource pageResource = new EuGrantPageResource();
        pageResource.setContent(euGrantResources);

        when(euInviteRestService.getEuGrantsByNotified(true, 0, 100))
                .thenReturn(restSuccess(pageResource));

        MvcResult result = mockMvc.perform(get("/eu-invite-notified"))
                .andExpect(status().isOk())
                .andExpect(view().name("eu/notified"))
                .andReturn();

        verify(euInviteRestService).getEuGrantsByNotified(true, 0, 100);

        EuInviteViewModel model = (EuInviteViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(euGrantResources, model.getGrants());
    }

    @Test
    public void nonNotified() throws Exception {

        List<EuGrantResource> euGrantResources = singletonList(new EuGrantResource());

        EuGrantPageResource pageResource = new EuGrantPageResource();
        pageResource.setContent(euGrantResources);

        when(euInviteRestService.getEuGrantsByNotified(false, 0, 100))
                .thenReturn(restSuccess(pageResource));

        MvcResult result = mockMvc.perform(get("/eu-invite-non-notified"))
                .andExpect(status().isOk())
                .andExpect(view().name("eu/non-notified"))
                .andReturn();

        verify(euInviteRestService).getEuGrantsByNotified(false, 0, 100);

        EuInviteViewModel model = (EuInviteViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(euGrantResources, model.getGrants());
    }

    @Test
    public void sendInvites() throws Exception {
        List<UUID> euGrantUuids = asList(
                new UUID(1L, 1L),
                new UUID(1L, 1L),
                new UUID(1L, 1L));

        when(euInviteRestService.sendInvites(euGrantUuids))
                .thenReturn(restSuccess());

        mockMvc.perform(post("/eu-send-invites")
                                .contentType(APPLICATION_FORM_URLENCODED)
                                .param("euGrantIds[0]", euGrantUuids.get(1).toString())
                                .param("euGrantIds[1]", euGrantUuids.get(1).toString())
                                .param("euGrantIds[2]", euGrantUuids.get(1).toString()))
                .andExpect(status().is3xxRedirection());

        verify(euInviteRestService).sendInvites(euGrantUuids);
    }

    @Override
    protected EuInviteController supplyControllerUnderTest() {
        return new EuInviteController();
    }
}