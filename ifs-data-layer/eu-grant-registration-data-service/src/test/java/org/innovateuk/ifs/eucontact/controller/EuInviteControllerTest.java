package org.innovateuk.ifs.eucontact.controller;

import org.innovateuk.ifs.MockMvcTest;
import org.innovateuk.ifs.eugrant.EuGrantPageResource;
import org.innovateuk.ifs.eugrant.transactional.EuGrantService;
import org.innovateuk.ifs.euinvite.controller.EuInviteController;
import org.innovateuk.ifs.euinvite.transactional.EuInviteService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

public class EuInviteControllerTest extends MockMvcTest<EuInviteController> {

    @Override
    public EuInviteController supplyControllerUnderTest() {
        return new EuInviteController();
    }

    @Mock
    private EuInviteService euInviteService;

    @Mock
    private EuGrantService euGrantService;

    final static boolean NOTIFIED = false;
    final static int PAGE_SIZE = 100;
    final static int PAGE_INDEX = 0;

    @Test
    public void getByNotified() throws Exception {

        EuGrantPageResource euGrantPageResource = new EuGrantPageResource();
        Pageable pageable = new PageRequest(PAGE_INDEX, PAGE_SIZE, new Sort("id"));


        when(euGrantService.getEuGrantsByContactNotified(NOTIFIED, pageable))
                .thenReturn(serviceSuccess(euGrantPageResource));

        mockMvc.perform(
                get("/eu-contacts/notified/{notified}", NOTIFIED))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(euGrantPageResource)));

        verify(euGrantService).getEuGrantsByContactNotified(NOTIFIED, pageable);
    }

    @Test
    public void sendInvites() throws Exception {

        List<Long> euContactInviteIds = asList(9L, 99L, 999L);
        UUID uuid1 = new UUID(1L, 1L);
        UUID uuid2 = new UUID(1L, 1L);
        UUID uuid3 = new UUID(1L, 1L);
        List<UUID> euGrantUuids = asList(uuid1, uuid2, uuid3);
        when(euInviteService.sendInvites(euGrantUuids)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/eu-contacts/send-invites")
                .content(json(euContactInviteIds))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(euInviteService).sendInvites(euGrantUuids);
    }
}