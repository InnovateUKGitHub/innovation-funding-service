package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.invite.resource.EuContactPageResource;
import org.innovateuk.ifs.invite.resource.EuContactResource;
import org.innovateuk.ifs.invite.viewmodel.EuInviteViewModel;
import org.innovateuk.ifs.user.service.EuContactRestService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.invite.builder.EuContactPageResourceBuilder.newEuContactPageResource;
import static org.innovateuk.ifs.invite.builder.EuContactResourceBuilder.newEuContactResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class EuInviteControllerTest extends BaseControllerMockMVCTest<EuInviteController> {

    @Mock
    private EuContactRestService euContactRestService;

    @Test
    public void notified() throws Exception {

        List<EuContactResource> euContactResources = newEuContactResource().build(2);
        EuContactPageResource pageResource = newEuContactPageResource()
                .withContent(euContactResources).build();

        when(euContactRestService.getEuContactsByNotified(true, 0,100))
                .thenReturn(restSuccess(pageResource));

        MvcResult result = mockMvc.perform(get("/eu-invite-notified"))
                .andExpect(status().isOk())
                .andExpect(view().name("eu/notified"))
                .andReturn();

        verify(euContactRestService).getEuContactsByNotified(true, 0, 100);

        EuInviteViewModel model = (EuInviteViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(euContactResources, model.getContacts());
    }

    @Test
    public void nonNotified() throws Exception {
        List<EuContactResource> euContactResources = newEuContactResource().build(2);
        EuContactPageResource pageResource = newEuContactPageResource()
                .withContent(euContactResources).build();

        when(euContactRestService.getEuContactsByNotified(false, 0,100))
                .thenReturn(restSuccess(pageResource));

        MvcResult result = mockMvc.perform(get("/eu-invite-non-notified"))
                .andExpect(status().isOk())
                .andExpect(view().name("eu/non-notified"))
                .andReturn();

        verify(euContactRestService).getEuContactsByNotified(false, 0, 100);

        EuInviteViewModel model = (EuInviteViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(euContactResources, model.getContacts());
    }

    @Override
    protected EuInviteController supplyControllerUnderTest() {
        return new EuInviteController();
    }
}
