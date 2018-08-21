package org.innovateuk.ifs.eugrant.controller;

import org.innovateuk.ifs.MockMvcTest;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.transactional.EuGrantService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.eugrant.builder.EuGrantResourceBuilder.newEuGrantResource;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EuGrantControllerTest extends MockMvcTest<EuGrantController> {

    @Mock
    private EuGrantService euGrantService;

    @Override
    public EuGrantController supplyControllerUnderTest() {
        return new EuGrantController();
    }

    @Test
    public void save() throws Exception {
        EuGrantResource euGrantResource = newEuGrantResource().build();

        when(euGrantService.save(euGrantResource)).thenReturn(serviceSuccess(euGrantResource));

        mockMvc.perform(
                post("/eu-grant")
                        .content(json(euGrantResource))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(euGrantService).save(euGrantResource);
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
}
