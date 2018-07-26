package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.finance.domain.GrantClaimMaximum;
import org.innovateuk.ifs.finance.mapper.GrantClaimMaximumMapper;
import org.innovateuk.ifs.finance.repository.GrantClaimMaximumRepository;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.finance.transactional.GrantClaimMaximumService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.finance.builder.GrantClaimMaximumResourceBuilder.newGrantClaimMaximumResource;
import static org.innovateuk.ifs.finance.domain.builder.GrantClaimMaximumBuilder.newGrantClaimMaximum;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GrantClaimMaximumControllerTest extends BaseControllerMockMVCTest<GrantClaimMaximumController> {

    @Mock
    private GrantClaimMaximumService grantClaimMaximumService;


    @Override
    protected GrantClaimMaximumController supplyControllerUnderTest() {
        return new GrantClaimMaximumController();
    }

    @Test
    public void getGrantClaimMaximumById() throws Exception {
        final Long gcmId = 1L;

        GrantClaimMaximumResource gcm = newGrantClaimMaximumResource().withId(gcmId).build();
        when(grantClaimMaximumService.getGrantClaimMaximumById(gcmId)).thenReturn(serviceSuccess(gcm));

        mockMvc.perform(get("/grantClaimMaximum/{id}", gcmId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(gcm)));

        verify(grantClaimMaximumService, only()).getGrantClaimMaximumById(gcmId);
    }

    @Test
    public void getGrantClaimMaximumByIdNotFound() throws Exception {
        when(grantClaimMaximumService.getGrantClaimMaximumById(1L)).thenReturn(serviceFailure(notFoundError(GrantClaimMaximum.class, 1L)));
        mockMvc.perform(get("/grantClaimMaximum/{id}", 1L))
                .andExpect(status().isNotFound());

        verify(grantClaimMaximumService, only()).getGrantClaimMaximumById(1L);
    }

    @Test
    public void save() throws Exception {
        GrantClaimMaximumResource gcmResource = newGrantClaimMaximumResource().build();

        when(grantClaimMaximumService.save(any(GrantClaimMaximumResource.class))).thenReturn(serviceSuccess(gcmResource));

        mockMvc.perform(post("/grantClaimMaximum/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson((gcmResource))))
                .andExpect(status().isCreated());

        verify(grantClaimMaximumService, only()).save(any(GrantClaimMaximumResource.class));
    }
}
