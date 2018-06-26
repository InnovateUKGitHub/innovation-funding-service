package org.innovateuk.ifs.form.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionType;
import org.innovateuk.ifs.form.controller.QuestionController;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.transactional.QuestionService;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionType.APPLICATION_TEAM;
import static org.innovateuk.ifs.documentation.QuestionDocs.questionBuilder;
import static org.innovateuk.ifs.documentation.QuestionDocs.questionFields;
import static org.innovateuk.ifs.form.resource.FormInputType.TEXTAREA;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

public class QuestionControllerDocumentation extends BaseControllerMockMVCTest<QuestionController> {

    @Override
    protected QuestionController supplyControllerUnderTest() {
        return new QuestionController();
    }

    @Mock
    QuestionService questionService;

    @Test
    public void getById() throws Exception {
        Long id = 1L;

        when(questionService.getQuestionById(id)).thenReturn(serviceSuccess(questionBuilder.build()));

        mockMvc.perform(get("/question/id/{id}", id))
                .andDo(document("question/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("id of the question to be found")
                        ),
                        responseFields(
                                questionFields
                        )
                ));
    }

    @Test
    public void findByCompetition() throws Exception {
        final Long competitionId = 1L;

        when(questionService.findByCompetition(competitionId)).thenReturn(serviceSuccess(questionBuilder.build(2)));

        mockMvc.perform(get("/question/findByCompetition/{competitionId}", competitionId))
                .andDo(document("question/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition for which the questions are requested")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("List of questions belonging to the competition")
                        )
                ));
    }

    @Test
    public void getNextQuestion() throws Exception {
        final Long questionId = 1L;

        when(questionService.getNextQuestion(questionId)).thenReturn(serviceSuccess(questionBuilder.build()));

        mockMvc.perform(get("/question/getNextQuestion/{questionId}", questionId))
                .andDo(document("question/{method-name}",
                        pathParameters(
                                parameterWithName("questionId").description("Id of the current question")
                        ),
                        responseFields(questionFields)
                ));
    }

    @Test
    public void getNextQuestionBySection() throws Exception {
        final Long sectionId = 1L;

        when(questionService.getNextQuestionBySection(sectionId)).thenReturn(serviceSuccess(questionBuilder.build()));

        mockMvc.perform(get("/question/getNextQuestionBySection/{sectionId}", sectionId))
                .andDo(document("question/{method-name}",
                        pathParameters(
                                parameterWithName("sectionId").description("Id of the current section")
                        ),
                        responseFields(questionFields)
                ));
    }

    @Test
    public void getPreviousQuestion() throws Exception {
        final Long questionId = 1L;

        when(questionService.getPreviousQuestion(questionId)).thenReturn(serviceSuccess(questionBuilder.build()));

        mockMvc.perform(get("/question/getPreviousQuestion/{questionId}", questionId))
                .andDo(document("question/{method-name}",
                        pathParameters(
                                parameterWithName("questionId").description("Id of the current question")
                        ),
                        responseFields(questionFields)
                ));
    }

    @Test
    public void getPreviousQuestionBySection() throws Exception {
        final Long sectionId = 1L;

        when(questionService.getPreviousQuestionBySection(sectionId)).thenReturn(serviceSuccess(questionBuilder.build()));

        mockMvc.perform(get("/question/getPreviousQuestionBySection/{sectionId}", sectionId))
                .andDo(document("question/{method-name}",
                        pathParameters(
                                parameterWithName("sectionId").description("Id of the current section")
                        ),
                        responseFields(questionFields)
                ));
    }

    @Test
    public void getQuestionByCompetitionIdAndFormInputType() throws Exception {
        FormInputType formInputType = TEXTAREA;
        Long competitionId = 123L;

        when(questionService.getQuestionResourceByCompetitionIdAndFormInputType(competitionId, formInputType)).thenReturn(serviceSuccess(questionBuilder.build()));

        mockMvc.perform(get("/question/getQuestionByCompetitionIdAndFormInputType/{competitionId}/{formInputType}", competitionId, formInputType))
                .andDo(document("question/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("The id of the competition to which the returned Question will belong"),
                                parameterWithName("formInputType").description("form input type")
                        ),
                        responseFields(questionFields)
                ));
    }

    @Test
    public void save() throws Exception {
        QuestionResource questionResource = questionBuilder.build();

        when(questionService.save(questionResource)).thenReturn(serviceSuccess(questionResource));

        mockMvc.perform(put("/question/")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(questionResource)))
                .andDo(document("question/{method-name}",
                        responseFields(questionFields)
                ));

    }

    @Test
    public void getQuestionsByAssessmentId() throws Exception {
        final Long assessmentId = 1L;

        when(questionService.getQuestionsByAssessmentId(assessmentId)).thenReturn(serviceSuccess(asList(questionBuilder.build())));

        mockMvc.perform(get("/question/getQuestionsByAssessment/{assessmentId}", assessmentId))
                .andDo(document("question/{method-name}",
                        pathParameters(
                                parameterWithName("assessmentId").description("Id of the assessment for which questions should be returned for")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("An array of the questions which are visible for the specified assessment")
                        )
                ));
    }

    @Test
    public void getQuestionByCompetitionIdAndCompetitionSetupQuestionType() throws Exception {
        final Long competitionId = 1L;
        final CompetitionSetupQuestionType setupQuestionType = APPLICATION_TEAM;

        when(questionService.getQuestionByCompetitionIdAndCompetitionSetupQuestionType(competitionId,
                setupQuestionType)).thenReturn(serviceSuccess(questionBuilder.build()));

        mockMvc.perform(get("/question/getQuestionByCompetitionIdAndCompetitionSetupQuestionType/{competitionId}/{type}",
                            competitionId, setupQuestionType))
                .andDo(document("question/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition for which a question should be returned for"),
                                parameterWithName("type").description("CompetitionSetupQuestionType of the question we want to be returned")
                        ),
                        responseFields(questionFields)
                ));
    }
}
