package org.innovateuk.ifs.form.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.form.controller.QuestionController;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.transactional.QuestionService;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.QuestionDocs.questionBuilder;
import static org.innovateuk.ifs.form.resource.FormInputType.TEXTAREA;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.APPLICATION_TEAM;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;

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

        mockMvc.perform(get("/question/id/{id}", id)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void findByCompetition() throws Exception {
        final Long competitionId = 1L;

        when(questionService.findByCompetition(competitionId)).thenReturn(serviceSuccess(questionBuilder.build(2)));

        mockMvc.perform(get("/question/find-by-competition/{competitionId}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void getNextQuestion() throws Exception {
        final Long questionId = 1L;

        when(questionService.getNextQuestion(questionId)).thenReturn(serviceSuccess(questionBuilder.build()));

        mockMvc.perform(get("/question/get-next-question/{questionId}", questionId)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void getNextQuestionBySection() throws Exception {
        final Long sectionId = 1L;

        when(questionService.getNextQuestionBySection(sectionId)).thenReturn(serviceSuccess(questionBuilder.build()));

        mockMvc.perform(get("/question/get-next-question-by-section/{sectionId}", sectionId)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void getPreviousQuestion() throws Exception {
        final Long questionId = 1L;

        when(questionService.getPreviousQuestion(questionId)).thenReturn(serviceSuccess(questionBuilder.build()));

        mockMvc.perform(get("/question/get-previous-question/{questionId}", questionId)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void getPreviousQuestionBySection() throws Exception {
        final Long sectionId = 1L;

        when(questionService.getPreviousQuestionBySection(sectionId)).thenReturn(serviceSuccess(questionBuilder.build()));

        mockMvc.perform(get("/question/get-previous-question-by-section/{sectionId}", sectionId)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void getQuestionByCompetitionIdAndFormInputType() throws Exception {
        FormInputType formInputType = TEXTAREA;
        Long competitionId = 123L;

        when(questionService.getQuestionResourceByCompetitionIdAndFormInputType(competitionId, formInputType)).thenReturn(serviceSuccess(questionBuilder.build()));

        mockMvc.perform(get("/question/get-question-by-competition-id-and-form-input-type/{competitionId}/{formInputType}", competitionId, formInputType)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void save() throws Exception {
        QuestionResource questionResource = questionBuilder.build();

        when(questionService.save(questionResource)).thenReturn(serviceSuccess(questionResource));

        mockMvc.perform(put("/question/")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(questionResource))
                .header("IFS_AUTH_TOKEN", "123abc"));

    }

    @Test
    public void getQuestionsByAssessmentId() throws Exception {
        final Long assessmentId = 1L;

        when(questionService.getQuestionsByAssessmentId(assessmentId)).thenReturn(serviceSuccess(asList(questionBuilder.build())));

        mockMvc.perform(get("/question/get-questions-by-assessment/{assessmentId}", assessmentId)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void getQuestionByCompetitionIdAndQuestionSetupType() throws Exception {
        final Long competitionId = 1L;
        final QuestionSetupType setupQuestionType = APPLICATION_TEAM;

        when(questionService.getQuestionByCompetitionIdAndQuestionSetupType(competitionId,
                setupQuestionType)).thenReturn(serviceSuccess(questionBuilder.build()));

        mockMvc.perform(get("/question/get-question-by-competition-id-and-question-setup-type/{competitionId}/{type}",
                            competitionId, setupQuestionType)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }
}
