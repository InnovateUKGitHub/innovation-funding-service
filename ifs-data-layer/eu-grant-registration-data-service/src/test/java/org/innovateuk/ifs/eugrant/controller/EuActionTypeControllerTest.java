package org.innovateuk.ifs.eugrant.controller;

import org.innovateuk.ifs.MockMvcTest;
import org.innovateuk.ifs.eugrant.EuActionTypeController;
import org.innovateuk.ifs.eugrant.EuActionTypeResource;
import org.innovateuk.ifs.eugrant.transactional.EuActionTypeService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.eugrant.builder.EuActionTypeResourceBuilder.newEuActionTypeResource;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EuActionTypeControllerTest extends MockMvcTest<EuActionTypeController> {

    @Mock
    private EuActionTypeService euActionTypeService;

    @Override
    public EuActionTypeController supplyControllerUnderTest() {
        return new EuActionTypeController();
    }

    @Test
    public void getById() throws Exception {
        EuActionTypeResource euActionTypeResource = newEuActionTypeResource()
                .withId(1L)
                .build();

        when(euActionTypeService.getById(euActionTypeResource.getId())).thenReturn(serviceSuccess(euActionTypeResource));

        mockMvc.perform(
                get("/eu-grant/action-type/get-by-id/{id}", euActionTypeResource.getId())
                        .content(json(euActionTypeResource))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(euActionTypeService).getById(euActionTypeResource.getId());
    }

    @Test
    public void findAll() throws Exception {
        List<EuActionTypeResource> euActionTypeResources = newEuActionTypeResource()
                .build(2);

        when(euActionTypeService.findAll()).thenReturn(serviceSuccess(euActionTypeResources));

        mockMvc.perform(
                get("/eu-grant/action-type/find-all")
                        .content(json(euActionTypeResources))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(euActionTypeService).findAll();
    }
}
