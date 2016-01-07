package com.worth.ifs.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.transactional.QuestionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.mockito.Mock;

import static com.worth.ifs.application.builder.QuestionBuilder.newQuestion;
import static com.worth.ifs.application.builder.SectionBuilder.newSection;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
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
        Question question = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 1).build();
        Question nextQuestion = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 2).build();
        when(questionService.getNextQuestion(question.getId())).thenReturn(nextQuestion);
        mockMvc.perform(get("/question/getNextQuestion/" + question.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(nextQuestion)))
                .andDo(document("question/next-question")).andReturn();
    }

    @Test
    public void getPreviousQuestionTest() throws Exception {
        Question question = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 2).build();
        Question previousQuestion = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 1).build();
        when(questionService.getPreviousQuestion(question.getId())).thenReturn(previousQuestion);
        mockMvc.perform(get("/question/getPreviousQuestion/" + question.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(previousQuestion)))
                .andDo(document("question/next-question"));
    }

    @Test
    public void getPreviousQuestionFromOtherSectionTest() throws Exception {
        Question question = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 2).build();
        Question previousQuestion = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 1).build();
        when(questionService.getPreviousQuestion(question.getId())).thenReturn(previousQuestion);
        mockMvc.perform(get("/question/getPreviousQuestion/" + question.getId()))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(previousQuestion)))
                .andExpect(status().isOk());
    }

    @Test
    public void getPreviousQuestionBySectionTest() throws Exception {
        Section currentSection = newSection().withCompetitionAndPriorityAndParent(newCompetition().build(), 1, newSection().build()).build();
        Question previousSectionQuestion = newQuestion().build();
        when(questionService.getPreviousQuestionBySection(currentSection.getId())).thenReturn(previousSectionQuestion);
        mockMvc.perform(get("/question/getPreviousQuestionBySection/" + currentSection.getId()))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(previousSectionQuestion)))
                .andExpect(status().isOk());
    }
}
