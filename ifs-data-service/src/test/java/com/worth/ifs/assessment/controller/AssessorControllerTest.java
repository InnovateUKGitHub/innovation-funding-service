package com.worth.ifs.assessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.BuilderAmendFunctions;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.RestErrorResponse;
import com.worth.ifs.invite.domain.CompetitionInvite;
import com.worth.ifs.registration.resource.UserRegistrationResource;
import org.junit.Test;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
import static com.worth.ifs.registration.builder.UserRegistrationResourceBuilder.newUserRegistrationResource;
import static com.worth.ifs.user.resource.Disability.NO;
import static com.worth.ifs.user.resource.Gender.NOT_STATED;
import static com.worth.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

        UserRegistrationResource userRegistrationResource = newUserRegistrationResource()
                .withTitle("Mr")
                .withFirstName("First")
                .withLastName("Last")
                .withPhoneNumber("01234 56789890")
                .withGender(NOT_STATED)
                .withDisability(NO)
                .withEthnicity(newEthnicityResource().with(BuilderAmendFunctions.id(1L)).build())
                .withPassword("Passw0rd123")
                .build();

        when(assessorServiceMock.registerAssessorByHash(hash, userRegistrationResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/assessor/register/{hash}", hash)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegistrationResource)))
                .andExpect(status().isOk());

        verify(assessorServiceMock, only()).registerAssessorByHash(hash, userRegistrationResource);
    }

    @Test
    public void registerAssessorByHash_returnsErrorOnFailure() throws Exception {
        String hash = "testhash";

        UserRegistrationResource userRegistrationResource = newUserRegistrationResource()
                .withTitle("Mr")
                .withFirstName("First")
                .withLastName("Last")
                .withPhoneNumber("01234 56789890")
                .withGender(NOT_STATED)
                .withDisability(NO)
                .withEthnicity(newEthnicityResource().with(BuilderAmendFunctions.id(1L)).build())
                .withPassword("Passw0rd123")
                .build();

        Error notFoundError = notFoundError(CompetitionInvite.class, hash);

        when(assessorServiceMock.registerAssessorByHash(hash, userRegistrationResource)).thenReturn(serviceFailure(notFoundError));

        mockMvc.perform(post("/assessor/register/{hash}", hash)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegistrationResource)))
                .andExpect(status().isNotFound())
                .andExpect(content().json(toJson(new RestErrorResponse(notFoundError))))
                .andReturn();

        verify(assessorServiceMock, only()).registerAssessorByHash(hash, userRegistrationResource);
    }

}