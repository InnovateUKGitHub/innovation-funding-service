package com.worth.ifs.application.documentation;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.controller.AssessorFeedbackController;
import com.worth.ifs.application.resource.AssessorFeedbackResource;
import com.worth.ifs.application.transactional.AssessorFeedbackService;

import org.junit.Test;
import org.mockito.Mock;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.documentation.AssessorFeedbackDocs.assessorFeedbackResourceBuilder;
import static com.worth.ifs.documentation.AssessorFeedbackDocs.assessorFeedbackResourceFields;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

public class AssessorFeedbackControllerDocumentation extends BaseControllerMockMVCTest<AssessorFeedbackController> {
    @Override
    protected AssessorFeedbackController supplyControllerUnderTest() {
        return new AssessorFeedbackController();
    }

    @Mock
    AssessorFeedbackService assessorFeedbackService;

    @Test
    public void documentFindById() throws Exception {
        Long id = 1L;
        AssessorFeedbackResource assessorFeedback = assessorFeedbackResourceBuilder.build();

        when(assessorFeedbackService.findOne(id)).thenReturn(serviceSuccess(assessorFeedback));
        mockMvc.perform(get("/assessorfeedback/{id}", id))
                .andDo(document("assessor-feedback/find-one",
                        pathParameters(
                                parameterWithName("id").description("Id of the assessor feedback to be found")
                        ),
                        responseFields(assessorFeedbackResourceFields)
                ));
    }
}
