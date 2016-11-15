package com.worth.ifs.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.resource.QuestionType;
import com.worth.ifs.application.transactional.QuestionService;
import com.worth.ifs.competition.domain.Competition;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import java.util.List;

import static com.worth.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static com.worth.ifs.application.builder.SectionBuilder.newSection;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

    public class QuestionControllerTest extends BaseControllerMockMVCTest<QuestionController> {

    @Mock
    protected QuestionService questionService;

    @Override
    protected QuestionController supplyControllerUnderTest() {
        return new QuestionController();
    }

    @Test
    public void getNextQuestionTest() throws Exception {
        Competition competition = newCompetition().build();
        Section section = newSection().build();
        QuestionResource nextQuestion = newQuestionResource().withCompetitionAndSectionAndPriority(competition, section, 2).build();
        when(questionService.getNextQuestion(anyLong())).thenReturn(serviceSuccess(nextQuestion));
        mockMvc.perform(get("/question/getNextQuestion/" + 1L))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(nextQuestion)))
                .andReturn();
    }

    @Test
    public void getPreviousQuestionTest() throws Exception {
        Competition competition = newCompetition().build();
        Section section = newSection().build();
        QuestionResource previousQuestion = newQuestionResource().withCompetitionAndSectionAndPriority(competition, section, 2).build();

        when(questionService.getPreviousQuestion(anyLong())).thenReturn(serviceSuccess(previousQuestion));

        mockMvc.perform(get("/question/getPreviousQuestion/" + previousQuestion.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(previousQuestion)));
    }

    @Test
    public void getPreviousQuestionFromOtherSectionTest() throws Exception {
        Competition competition = newCompetition().build();
        Section section = newSection().build();
        QuestionResource previousQuestion = newQuestionResource().withCompetitionAndSectionAndPriority(competition, section, 1).build();

        when(questionService.getPreviousQuestion(anyLong())).thenReturn(serviceSuccess(previousQuestion));

        mockMvc.perform(get("/question/getPreviousQuestion/" + 1L))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(previousQuestion)))
                .andExpect(status().isOk());
    }

    @Test
    public void getPreviousQuestionBySectionTest() throws Exception {
        QuestionResource previousSectionQuestion = newQuestionResource().build();

        when(questionService.getPreviousQuestionBySection(anyLong())).thenReturn(serviceSuccess(previousSectionQuestion));

        mockMvc.perform(get("/question/getPreviousQuestionBySection/" + 1L))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(previousSectionQuestion)))
                .andExpect(status().isOk());
    }

    @Test
    public void getQuestionsBySectionIdAndTypeTest() throws Exception {
        List<QuestionResource> questions = newQuestionResource().build(2);

        when(questionService.getQuestionsBySectionIdAndType(1L, QuestionType.COST)).thenReturn(serviceSuccess(questions));

        mockMvc.perform(get("/question/getQuestionsBySectionIdAndType/1/COST"))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(questions)))
                .andExpect(status().isOk());
    }

    @Test
    public void save() throws Exception {
        QuestionResource questionResource = newQuestionResource().build();

        when(questionService.save(questionResource)).thenReturn(serviceSuccess(questionResource));

        mockMvc.perform(put("/question/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(questionResource)))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(questionResource)))
                .andExpect(status().isOk());
    }

    @Test
    public void getQuestionByIdAndAssessmentId() throws Exception {
        Long questionId = 1L;
        Long assessmentId = 2L;

        QuestionResource questionResource = newQuestionResource().build();

        when(questionService.getQuestionByIdAndAssessmentId(questionId, assessmentId)).thenReturn(serviceSuccess(questionResource));

        mockMvc.perform(get("/question/getQuestionByIdAndAssessmentId/{questionId}/{assessmentId}", questionId, assessmentId))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(questionResource)))
                .andExpect(status().isOk());

        verify(questionService, only()).getQuestionByIdAndAssessmentId(questionId, assessmentId);
    }

    @Test
    public void getQuestionsByAssessmentId() throws Exception {
        final Long assessmentId = 1L;

        List<QuestionResource> questions = newQuestionResource().build(2);

        when(questionService.getQuestionsByAssessmentId(assessmentId)).thenReturn(serviceSuccess(questions));

        mockMvc.perform(get("/question/getQuestionsByAssessment/{assessmentId}", assessmentId))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(questions)))
                .andExpect(status().isOk());
    }
}
