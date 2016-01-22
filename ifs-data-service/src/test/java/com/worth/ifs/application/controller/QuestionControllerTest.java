package com.worth.ifs.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.transactional.QuestionService;
import com.worth.ifs.competition.domain.Competition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.mockito.Mock;

import static com.worth.ifs.application.builder.QuestionBuilder.newQuestion;
import static com.worth.ifs.application.builder.SectionBuilder.newSection;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class QuestionControllerTest extends BaseControllerMockMVCTest<QuestionController> {
    private final Log log = LogFactory.getLog(getClass());

    @Mock
    protected SectionController sectionController;

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
        Question nextQuestion = newQuestion().withCompetitionAndSectionAndPriority(competition, section, 2).build();
        when(questionService.getNextQuestion(anyLong())).thenReturn(nextQuestion);
        mockMvc.perform(get("/question/getNextQuestion/" + 1L))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(nextQuestion)))
                .andDo(document("question/next-question")).andReturn();
    }

    @Test
    public void getPreviousQuestionTest() throws Exception {
        Competition competition = newCompetition().build();
        Section section = newSection().build();
        Question previousQuestion = newQuestion().withCompetitionAndSectionAndPriority(competition, section, 2).build();

        when(questionService.getPreviousQuestion(anyLong())).thenReturn(previousQuestion);

        mockMvc.perform(get("/question/getPreviousQuestion/" + previousQuestion.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(previousQuestion)))
                .andDo(document("question/next-question"));
    }

    @Test
    public void getPreviousQuestionFromOtherSectionTest() throws Exception {
        Competition competition = newCompetition().build();
        Section section = newSection().build();
        Question previousQuestion = newQuestion().withCompetitionAndSectionAndPriority(competition, section, 1).build();

        when(questionService.getPreviousQuestion(anyLong())).thenReturn(previousQuestion);

        mockMvc.perform(get("/question/getPreviousQuestion/" + 1L))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(previousQuestion)))
                .andExpect(status().isOk());
    }

    @Test
    public void getPreviousQuestionBySectionTest() throws Exception {
        Question previousSectionQuestion = newQuestion().build();

        when(questionService.getPreviousQuestionBySection(anyLong())).thenReturn(previousSectionQuestion);

        mockMvc.perform(get("/question/getPreviousQuestionBySection/" + 1L))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(previousSectionQuestion)))
                .andExpect(status().isOk());
    }
}
