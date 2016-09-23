package com.worth.ifs.assessment.documentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.controller.AssessorController;
import com.worth.ifs.registration.resource.UserRegistrationResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.documentation.UserRegistrationResourceDocs.userRegistrationResourceBuilder;
import static com.worth.ifs.documentation.UserRegistrationResourceDocs.userRegistrationResourceFields;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AssessorControllerDocumentation extends BaseControllerMockMVCTest<AssessorController> {

    private RestDocumentationResultHandler document;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected AssessorController supplyControllerUnderTest() {
        return new AssessorController();
    }

    @Before
    public void setup() {
        this.document = document("assessor/{method-name}",
                preprocessResponse(prettyPrint()));
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
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("hash").description("hash of the invite being accepted")
                        ),
                        requestFields(userRegistrationResourceFields)
                ));

        verify(assessorServiceMock).registerAssessorByHash(hash, userRegistrationResource);
    }
}