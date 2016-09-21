package com.worth.ifs.assessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Test;

import static com.worth.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AssessorControllerTest extends BaseControllerMockMVCTest<AssessorController> {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected AssessorController supplyControllerUnderTest() {
        return new AssessorController();
    }

    @Test
    public void registerAssessorByHash() throws Exception {
        String hash = "testhash";
        UserResource userResource = newUserResource().build();

        when(assessorServiceMock.registerAssessorByHash(hash, userResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/assessor/register/{hash}", hash)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userResource)))
                .andExpect(status().isOk());

        verify(assessorServiceMock, only()).registerAssessorByHash(hash, userResource);
    }

    @Test
    public void registerAssessorByHash_returnsErrorOnFailure() throws Exception {
        String hash = "testhash";
        UserResource userResource = newUserResource().build();

        Error notFoundError = new Error(GENERAL_NOT_FOUND, "invite not found", "", NOT_FOUND);

        when(assessorServiceMock.registerAssessorByHash(hash, userResource)).thenReturn(serviceFailure(notFoundError));

        mockMvc.perform(post("/assessor/register/{hash}", hash)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userResource)))
                .andExpect(status().is4xxClientError());

        verify(assessorServiceMock, only()).registerAssessorByHash(hash, userResource);
    }

}