package org.innovateuk.ifs.eucontact.controller;

import org.innovateuk.ifs.MockMvcTest;
import org.innovateuk.ifs.eucontact.transactional.EuContactService;
import org.innovateuk.ifs.eugrant.EuContactPageResource;
import org.innovateuk.ifs.euinvite.EuInviteService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;

import java.util.List;

import static com.google.common.primitives.Longs.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

public class EuContactControllerTest extends MockMvcTest<EuContactController> {

    @Override
    public EuContactController supplyControllerUnderTest() {
        return new EuContactController();
    }

    @Mock
    private EuContactService euContactService;

    @Mock
    private EuInviteService euInviteService;

    final static boolean NOTIFIED = false;
    final static int PAGE_SIZE = 100;
    final static int PAGE_INDEX = 0;

    @Test
    public void getByNotified() throws Exception {

        EuContactPageResource euContactPageResource = new EuContactPageResource();
        Pageable pageable = new PageRequest(PAGE_INDEX, PAGE_SIZE, new Sort("id"));


        when(euContactService.getEuContactsByNotified(NOTIFIED, pageable))
                .thenReturn(serviceSuccess(euContactPageResource));

        mockMvc.perform(
                get("/eu-contacts/notified/{notified}", NOTIFIED))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(euContactPageResource)));

        verify(euContactService).getEuContactsByNotified(NOTIFIED, pageable);
    }

    @Test
    public void sendInvites() throws Exception {

        List<Long> euContactInviteIds = asList(9L, 99L, 999L);
        when(euInviteService.sendInvites(euContactInviteIds)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/eu-contacts/send-invites")
                .content(json(euContactInviteIds))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(euInviteService).sendInvites(euContactInviteIds);
    }
}