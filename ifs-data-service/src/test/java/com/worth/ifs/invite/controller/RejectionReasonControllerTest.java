package com.worth.ifs.invite.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.invite.resource.RejectionReasonResource;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.invite.builder.RejectionReasonResourceBuilder.newRejectionReasonResource;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RejectionReasonControllerTest extends BaseControllerMockMVCTest<RejectionReasonController> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected RejectionReasonController supplyControllerUnderTest() {
        return new RejectionReasonController();
    }

    @Test
    public void findAllActive() throws Exception {

        List<RejectionReasonResource> expected = newRejectionReasonResource().build(2);

        when(rejectionReasonServiceMock.findAllActive()).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/rejectionReason/findAllActive"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().string(objectMapper.writeValueAsString(expected)));

        verify(rejectionReasonServiceMock, only()).findAllActive();
    }

}