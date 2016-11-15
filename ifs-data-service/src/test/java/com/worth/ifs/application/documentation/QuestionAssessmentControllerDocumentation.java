package com.worth.ifs.application.documentation;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.controller.QuestionAssessmentController;
import com.worth.ifs.application.transactional.QuestionAssessmentService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.documentation.QuestionAssessmentDocs.questionAssesmentBuilder;
import static com.worth.ifs.documentation.QuestionAssessmentDocs.questionAssessmentFields;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

public class QuestionAssessmentControllerDocumentation extends BaseControllerMockMVCTest<QuestionAssessmentController> {
    private RestDocumentationResultHandler document;

    @Override
    protected QuestionAssessmentController supplyControllerUnderTest() {
        return new QuestionAssessmentController();
    }

    @Mock
    private QuestionAssessmentService questionAssessmentService;

    @Before
    public void setup(){
        this.document = document("questionAssessment/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void testGetById() throws Exception {
        Long id = 1L;
        when(questionAssessmentService.getById(id)).thenReturn(serviceSuccess(questionAssesmentBuilder.build()));

        mockMvc.perform(get("/questionAssessment/{id}", id))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("id").description("id of the assesment to be found")
                        ),
                        responseFields(
                                questionAssessmentFields
                        )
                ));
    }

    @Test
    public void testGetQuestById() throws Exception {
        Long id = 1L;
        when(questionAssessmentService.findByQuestion(id)).thenReturn(serviceSuccess(questionAssesmentBuilder.build()));

        mockMvc.perform(get("/questionAssessment/findByQuestion/{id}", id))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("id").description("id of the question to be found")
                        ),
                        responseFields(
                                questionAssessmentFields
                        )
                ));
    }

}