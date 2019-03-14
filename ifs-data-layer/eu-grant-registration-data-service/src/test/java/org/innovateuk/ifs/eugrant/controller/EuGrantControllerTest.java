package org.innovateuk.ifs.eugrant.controller;

import org.innovateuk.ifs.MockMvcTest;
import org.innovateuk.ifs.eugrant.EuGrantPageResource;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.transactional.EuGrantService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;

import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.eugrant.builder.EuGrantResourceBuilder.newEuGrantResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EuGrantControllerTest extends MockMvcTest<EuGrantController> {

    @Mock
    private EuGrantService euGrantService;

    @Override
    public EuGrantController supplyControllerUnderTest() {
        return new EuGrantController();
    }


    final static boolean NOTIFIED = false;
    final static int PAGE_SIZE = 100;
    final static int PAGE_INDEX = 0;

    @Test
    public void create() throws Exception {
        EuGrantResource euGrantResource = newEuGrantResource().build();

        when(euGrantService.create()).thenReturn(serviceSuccess(euGrantResource));

        mockMvc.perform(
                post("/eu-grant")
                        .content(json(euGrantResource))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(euGrantService).create();
    }

    @Test
    public void findById() throws Exception {
        EuGrantResource euGrantResource = newEuGrantResource().build();
        UUID uuid = randomUUID();

        when(euGrantService.findById(uuid)).thenReturn(serviceSuccess(euGrantResource));

        mockMvc.perform(
                get("/eu-grant/{uuid}", uuid.toString())
                        .content(json(euGrantResource))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(euGrantService).findById(uuid);
    }

    @Test
    public void getByNotified() throws Exception {

        EuGrantPageResource euGrantPageResource = new EuGrantPageResource();
        Pageable pageable = new PageRequest(PAGE_INDEX, PAGE_SIZE, new Sort("contact.id"));


        when(euGrantService.getEuGrantsByContactNotified(NOTIFIED, pageable))
                .thenReturn(serviceSuccess(euGrantPageResource));

        mockMvc.perform(
                get("/eu-grants/notified/{notified}", NOTIFIED))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(euGrantPageResource)));

        verify(euGrantService).getEuGrantsByContactNotified(NOTIFIED, pageable);
    }

    @Test
    public void getTotalSubmitted() throws Exception {

        long totalSubmitted = 987L;

        when(euGrantService.getTotalSubmitted())
                .thenReturn(serviceSuccess(totalSubmitted));

        mockMvc.perform(
                get("/eu-grants/total-submitted"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(totalSubmitted)));

        verify(euGrantService).getTotalSubmitted();
    }
}
