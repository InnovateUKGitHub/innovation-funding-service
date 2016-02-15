package com.worth.ifs.application.transactional;

import java.util.Arrays;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.mapper.SectionMapper;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.competition.domain.Competition;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import static com.worth.ifs.application.builder.QuestionBuilder.newQuestion;
import static com.worth.ifs.application.builder.SectionBuilder.newSection;
import static com.worth.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

public class QuestionServiceTest extends BaseUnitTestMocksTest {

    @Autowired
    SectionMapper sectionMapper;


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
        assertEquals(nextQuestion, questionService.getNextQuestion(question.getId()).getSuccessObject());
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
        assertEquals(previousQuestion, questionService.getPreviousQuestion(question.getId()).getSuccessObject());
    }

    @Test
    public void getNextQuestionFromOtherSectionTest() throws Exception {
        Section nextSection = newSection().build();
        SectionResource nextSectionResource = newSectionResource().build();
        Question question = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 1).build();
        Question nextQuestion = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), nextSection, 2).build();


        when(questionRepository.findOne(question.getId())).thenReturn(question);
        when(sectionService.getNextSection(any(SectionResource.class))).thenReturn(serviceSuccess(nextSectionResource));
        when(questionRepository.findFirstByCompetitionIdAndSectionIdAndPriorityGreaterThanOrderByPriorityAsc(
            question.getCompetition().getId(), question.getSection().getId(), question.getPriority())).thenReturn(nextQuestion);
        // Method under test
        assertEquals(nextQuestion, questionService.getNextQuestion(question.getId()).getSuccessObject());
    }

    @Test
    public void getPreviousQuestionFromOtherSectionTest() throws Exception {
        Section previousSection = newSection().build();
        SectionResource previousSectionResource = newSectionResource().build();
        Competition competition = newCompetition().build();
        Question question = newQuestion().withCompetitionAndSectionAndPriority(competition, newSection().build(), 2).build();
        Question previousQuestion = newQuestion().withCompetitionAndSectionAndPriority(competition, previousSection, 1).build();

        when(questionRepository.findOne(question.getId())).thenReturn(question);
        when(sectionService.getPreviousSection(any(SectionResource.class)))
                .thenReturn(serviceSuccess(previousSectionResource));
        when(questionRepository.findFirstByCompetitionIdAndSectionIdOrderByPriorityDesc(
                question.getCompetition().getId(), previousQuestion.getSection().getId()))
                .thenReturn(previousQuestion);
        when(questionRepository.findFirstByCompetitionIdAndSectionIdAndPriorityLessThanOrderByPriorityDesc(
            question.getCompetition().getId(), question.getSection().getId(), question.getPriority()))
            .thenReturn(previousQuestion);

        // Method under test
        assertEquals(previousQuestion, questionService.getPreviousQuestion(question.getId()).getSuccessObject());

    }

    @Test
    public void getPreviousQuestionBySectionTest() throws Exception {
        Section currentSection = newSection().withCompetitionAndPriorityAndParent(newCompetition().build(), 1, newSection().build()).build();
        SectionResource currentSectionResource = newSectionResource().withCompetitionAndPriorityAndParent(newCompetition().build().getId(), 1, newSection().build().getId()).build();
        Question previousSectionQuestion = newQuestion().build();
        Section previousSection = newSection().withQuestions(Arrays.asList(previousSectionQuestion)).build();
        SectionResource previousSectionResource = newSectionResource().withQuestions(Arrays.asList(previousSectionQuestion.getId())).build();
        when(sectionService.getById(currentSection.getId())).thenReturn(serviceSuccess(currentSectionResource));
        when(sectionService.getPreviousSection(currentSectionResource)).thenReturn(serviceSuccess(previousSectionResource));
        when(questionRepository.findOne(anyLong())).thenReturn(previousSectionQuestion);
        // Method under test
        assertEquals(previousSectionQuestion, questionService.getPreviousQuestionBySection(currentSection.getId()).getSuccessObject());


    }
}
