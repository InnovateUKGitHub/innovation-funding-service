package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.invite.resource.RejectionReasonResource;
import org.innovateuk.ifs.invite.transactional.RejectionReasonService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.RejectionReasonResourceBuilder.newRejectionReasonResource;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RejectionReasonControllerTest extends BaseControllerMockMVCTest<RejectionReasonController> {

    @Mock
    private RejectionReasonService rejectionReasonServiceMock;

    @Override
    protected RejectionReasonController supplyControllerUnderTest() {
        return new RejectionReasonController();
    }

    @Test
    public void findAllActive() throws Exception {

        List<RejectionReasonResource> expected = newRejectionReasonResource().build(2);

        when(rejectionReasonServiceMock.findAllActive()).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/rejection-reason/find-all-active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().string(objectMapper.writeValueAsString(expected)));

        verify(rejectionReasonServiceMock, only()).findAllActive();
    }

}
