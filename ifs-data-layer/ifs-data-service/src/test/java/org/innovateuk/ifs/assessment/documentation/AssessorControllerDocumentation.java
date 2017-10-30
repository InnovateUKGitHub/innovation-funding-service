package org.innovateuk.ifs.assessment.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.controller.AssessorController;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.junit.Test;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.AssessorProfileResourceDocs.assessorProfileResourceBuilder;
import static org.innovateuk.ifs.documentation.AssessorProfileResourceDocs.assessorProfileResourceFields;
import static org.innovateuk.ifs.documentation.UserRegistrationResourceDocs.userRegistrationResourceBuilder;
import static org.innovateuk.ifs.documentation.UserRegistrationResourceDocs.userRegistrationResourceFields;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AssessorControllerDocumentation extends BaseControllerMockMVCTest<AssessorController> {
    @Override
    protected AssessorController supplyControllerUnderTest() {
        return new AssessorController();
    }

    @Test
    public void registerAssessorByHash() throws Exception {
        String hash = "testhash";

        UserRegistrationResource userRegistrationResource = userRegistrationResourceBuilder.build();

        when(assessorServiceMock.registerAssessorByHash(hash, userRegistrationResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/assessor/register/{hash}", hash)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegistrationResource)))
                .andExpect(status().isOk())
                .andDo(document("assessor/register-assessor-by-hash",
                        pathParameters(
                                parameterWithName("hash").description("hash of the invite being accepted")
                        ),
                        requestFields(userRegistrationResourceFields)
                ));

        verify(assessorServiceMock).registerAssessorByHash(hash, userRegistrationResource);
    }

    @Test
    public void getAssessorProfile() throws Exception {
        Long assessorId = 1L;

        AssessorProfileResource assessorProfileResource = assessorProfileResourceBuilder.build();

        when(assessorServiceMock.getAssessorProfile(assessorId)).thenReturn(serviceSuccess(assessorProfileResource));

        mockMvc.perform(get("/assessor/profile/{assessorId}", assessorId))
                .andExpect(status().isOk())
                .andDo(document("assessor/get-assessor-profile",
                        pathParameters(
                                parameterWithName("assessorId").description("Id of the assessor")
                        ),
                        responseFields(assessorProfileResourceFields)
                ));
    }
}
