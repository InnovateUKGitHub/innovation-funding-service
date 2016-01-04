package com.worth.ifs.application.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Section;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;

import static com.worth.ifs.application.builder.QuestionBuilder.newQuestion;
import static com.worth.ifs.application.builder.SectionBuilder.newSection;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class QuestionServiceTest extends BaseUnitTestMocksTest {
    private final Log log = LogFactory.getLog(getClass());

    @InjectMocks
    protected QuestionService questionService = new QuestionServiceImpl();

    @Mock
    SectionService sectionService;

    @Test
    public void getNextQuestionTest() throws Exception {
        Question question = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 1).build();
        Question nextQuestion = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 2).build();

        when(questionRepository.findOne(question.getId())).thenReturn(question);
        when(questionRepository.findFirstByCompetitionIdAndSectionIdAndPriorityGreaterThanOrderByPriorityAsc(
                question.getCompetition().getId(), question.getSection().getId(), question.getPriority()))
                .thenReturn(nextQuestion);
        // Method under test
        assertEquals(nextQuestion, questionService.getNextQuestion(question.getId()));
    }

    @Test
    public void getPreviousQuestionTest() throws Exception {
        Question question = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 2).build();
        Question previousQuestion = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 1).build();

        when(questionRepository.findOne(question.getId())).thenReturn(question);
        when(questionRepository.findFirstByCompetitionIdAndSectionIdAndPriorityLessThanOrderByPriorityDesc(
                question.getCompetition().getId(), question.getSection().getId(), question.getPriority()))
                .thenReturn(previousQuestion);
        // Method under test
        assertEquals(previousQuestion, questionService.getPreviousQuestion(question.getId()));
    }

    @Test
    public void getNextQuestionFromOtherSectionTest() throws Exception {
        Question question = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 1).build();
        Question nextQuestion = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 2).build();

        when(questionRepository.findOne(question.getId())).thenReturn(question);
        when(sectionService.getNextSection(question.getSection()))
                .thenReturn(nextQuestion.getSection());
        when(questionRepository.findFirstByCompetitionIdAndSectionIdOrderByPriorityAsc(
                question.getCompetition().getId(), nextQuestion.getSection().getId()))
                .thenReturn(nextQuestion);
        // Method under test
        assertEquals(nextQuestion, questionService.getNextQuestion(question.getId()));
    }

    @Test
    public void getPreviousQuestionFromOtherSectionTest() throws Exception {
        Question question = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 2).build();
        Question previousQuestion = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 1).build();

        when(questionRepository.findOne(question.getId())).thenReturn(question);
        when(sectionService.getPreviousSection(question.getSection()))
                .thenReturn(previousQuestion.getSection());
        when(questionRepository.findFirstByCompetitionIdAndSectionIdOrderByPriorityDesc(
                question.getCompetition().getId(), previousQuestion.getSection().getId()))
                .thenReturn(previousQuestion);
        // Method under test
        assertEquals(previousQuestion, questionService.getPreviousQuestion(question.getId()));

    }

    @Test
    public void getPreviousQuestionBySectionTest() throws Exception {
        Section currentSection = newSection().withCompetitionAndPriorityAndParent(newCompetition().build(), 1, newSection().build()).build();
        Question previousSectionQuestion = newQuestion().build();
        Section previousSection = newSection().withQuestions(Arrays.asList(previousSectionQuestion)).build();
        when(sectionService.getById(currentSection.getId())).thenReturn(currentSection);
        when(sectionService.getPreviousSection(currentSection)).thenReturn(previousSection);
        // Method under test
        assertEquals(previousSectionQuestion, questionService.getPreviousQuestionBySection(currentSection.getId()));


    }
}
