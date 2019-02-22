package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.eu.invite.EuInviteRestService;
import org.innovateuk.ifs.eugrant.EuContactPageResource;
import org.innovateuk.ifs.eugrant.EuContactResource;
import org.innovateuk.ifs.invite.viewmodel.EuInviteViewModel;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

        when(euInviteRestService.getEuContactsByNotified(true, 0,100))
                .thenReturn(restSuccess(pageResource));

        MvcResult result = mockMvc.perform(get("/eu-invite-notified"))
                .andExpect(status().isOk())
                .andExpect(view().name("eu/notified"))
                .andReturn();

        verify(euInviteRestService).getEuContactsByNotified(true, 0, 100);

        EuInviteViewModel model = (EuInviteViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(euContactResources, model.getContacts());
    }

    @Test
    public void nonNotified() throws Exception {

        List<EuContactResource> euContactResources = singletonList(new EuContactResource());

        EuContactPageResource pageResource = new EuContactPageResource();
        pageResource.setContent(euContactResources);

        when(euInviteRestService.getEuContactsByNotified(false, 0,100))
                .thenReturn(restSuccess(pageResource));

        MvcResult result = mockMvc.perform(get("/eu-invite-non-notified"))
                .andExpect(status().isOk())
                .andExpect(view().name("eu/non-notified"))
                .andReturn();

        verify(euInviteRestService).getEuContactsByNotified(false, 0, 100);

        EuInviteViewModel model = (EuInviteViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(euContactResources, model.getContacts());
    }

    @Override
    protected EuInviteController supplyControllerUnderTest() {
        return new EuInviteController();
    }
}