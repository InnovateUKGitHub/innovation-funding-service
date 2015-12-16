package com.worth.ifs.application.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.form.repository.FormInputResponseRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class QuestionControllerTest extends BaseControllerMockMVCTest<QuestionController> {
    private final Log log = LogFactory.getLog(getClass());

    @Mock
    protected SectionController sectionController;

    @Override
    protected QuestionController supplyControllerUnderTest() {
        return new QuestionController();
    }

    @Test
    public void getNextQuestionTest() throws Exception {
        Question question = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 1).build();
        Question nextQuestion = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 2).build();

        when(questionRepository.findOne(1L)).thenReturn(question);
        when(questionRepository.findFirstByCompetitionIdAndSectionIdAndPriorityGreaterThanOrderByPriorityAsc(
                question.getCompetition().getId(), question.getSection().getId(), question.getPriority()))
                .thenReturn(nextQuestion);

        mockMvc.perform(get("/question/getNextQuestion/1"))
                .andExpect(status().isOk())
                .andDo(document("question/next-question"));
    }

    @Test
    public void getPreviousQuestionTest() throws Exception {
        Question question = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 2).build();
        Question previousQuestion = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 1).build();

        when(questionRepository.findOne(1L)).thenReturn(question);
        when(questionRepository.findFirstByCompetitionIdAndSectionIdAndPriorityLessThanOrderByPriorityDesc(
                question.getCompetition().getId(), question.getSection().getId(), question.getPriority()))
                .thenReturn(previousQuestion);

        mockMvc.perform(get("/question/getPreviousQuestion/1"))
                .andExpect(status().isOk())
                .andDo(document("question/next-question"));
    }

    @Test
    public void getNextQuestionFromOtherSectionTest() throws Exception {
        Question question = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 1).build();
        Question nextQuestion = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 2).build();

        when(questionRepository.findOne(1L)).thenReturn(question);
        when(sectionController.getNextSection(question.getSection()))
                .thenReturn(nextQuestion.getSection());
        when(questionRepository.findFirstByCompetitionIdAndSectionIdOrderByPriorityAsc(
                question.getCompetition().getId(), question.getSection().getId()))
                .thenReturn(nextQuestion);

        mockMvc.perform(get("/question/getNextQuestion/1"))
                .andExpect(status().isOk());

    }

    @Test
    public void getPreviousQuestionFromOtherSectionTest() throws Exception {
        Question question = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 2).build();
        Question previousQuestion = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 1).build();

        when(questionRepository.findOne(1L)).thenReturn(question);
        when(sectionController.getPreviousSection(question.getSection(), true, false))
                .thenReturn(previousQuestion.getSection());
        when(questionRepository.findFirstByCompetitionIdAndSectionIdOrderByPriorityDesc(
                question.getCompetition().getId(), question.getSection().getId()))
                .thenReturn(previousQuestion);

        mockMvc.perform(get("/question/getPreviousQuestion/1"))
                .andExpect(status().isOk());
    }
}
