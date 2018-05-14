package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.form.repository.QuestionRepository;
import org.innovateuk.ifs.question.transactional.template.QuestionNumberOrderService;
import org.innovateuk.ifs.question.transactional.template.QuestionPriorityOrderService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.innovateuk.ifs.form.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.setup.resource.QuestionSection.*;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.*;


public class QuestionPriorityServiceTest extends BaseServiceUnitTest<QuestionPriorityOrderService>{

    @Mock
    private QuestionRepository questionRepositoryMock;

    @Mock
    private QuestionNumberOrderService questionNumberOrderServiceMock;

    public QuestionPriorityOrderService supplyServiceUnderTest() {
        return new QuestionPriorityOrderService();
    }

    @Test
    public void prioritiseAssessedQuestionAfterCreation() throws Exception {
        Competition competition = newCompetition().withId(1L).build();

        List<Question> existingQuestions = newQuestion()
                .withId(1L, 2L, 3L, 4L)
                .withPriority(1, 2, 3, null)
                .withCompetition(competition)
                .build(4);

        Question lastPrioritizedQuestion = existingQuestions.get(2);
        Question newlyCreatedQuestion = existingQuestions.get(3);


        when(questionRepositoryMock.findFirstByCompetitionIdAndSectionNameOrderByPriorityDesc(lastPrioritizedQuestion.getCompetition().getId(),
                APPLICATION_QUESTIONS.getName())).thenReturn(lastPrioritizedQuestion);

        service.prioritiseAssessedQuestionAfterCreation(newlyCreatedQuestion);

        Question expectedQuestion = newlyCreatedQuestion;
        expectedQuestion.setPriority(4);

        verify(questionNumberOrderServiceMock).updateAssessedQuestionsNumbers(expectedQuestion.getCompetition().getId());
        verify(questionRepositoryMock, times(1)).save(refEq(expectedQuestion));
    }

    @Test
    public void reprioritiseAssessedQuestionsAfterDeletion_deleteMiddleQuestionShouldAffectOnlySubsequentPriorities() throws Exception {
        Competition competition = newCompetition().withId(1L).build();

        List<Question> existingQuestions = newQuestion()
                .withId(1L, 2L, 4L)
                .withPriority(1, 2, 4)
                .withCompetition(competition)
                .build(3);

        Question deletedQuestion = newQuestion()
                .withId(3L)
                .withPriority(3)
                .withCompetition(competition)
                .build();

        when(questionRepositoryMock.findByCompetitionIdAndSectionNameAndPriorityGreaterThanOrderByPriorityAsc(
                deletedQuestion.getCompetition().getId(),
                APPLICATION_QUESTIONS.getName(),
                deletedQuestion.getPriority()))
                .thenReturn(existingQuestions);

        service.reprioritiseAssessedQuestionsAfterDeletion(deletedQuestion);

        List<Question> expectedQuestions = newQuestion()
                .withId(1L, 2L, 4L)
                .withPriority(1, 2, 3)
                .withCompetition(competition)
                .build(3);

        verify(questionRepositoryMock).save(refEq(expectedQuestions));
    }

    @Test
    public void reprioritiseAssessedQuestionsAfterDeletion_deleteFirstQuestionShouldAffectAllPriorities() throws Exception {
        Competition competition = newCompetition().withId(1L).build();

        List<Question> existingQuestions = newQuestion()
                .withId(2L, 3L, 4L)
                .withPriority(2, 3, 4)
                .withCompetition(competition)
                .build(3);

        Question deletedQuestion = newQuestion()
                .withId(1L)
                .withPriority(1)
                .withCompetition(competition)
                .build();

        when(questionRepositoryMock.findByCompetitionIdAndSectionNameAndPriorityGreaterThanOrderByPriorityAsc(
                deletedQuestion.getCompetition().getId(),
                APPLICATION_QUESTIONS.getName(),
                deletedQuestion.getPriority()))
                .thenReturn(existingQuestions);

        service.reprioritiseAssessedQuestionsAfterDeletion(deletedQuestion);

        List<Question> expectedQuestions = newQuestion()
                .withId(2L, 3L, 4L)
                .withPriority(1, 2, 3)
                .withCompetition(competition)
                .build(3);

        verify(questionRepositoryMock).save(refEq(expectedQuestions));
    }

    @Test
    public void reprioritiseAssessedQuestionsAfterDeletion_deleteLastQuestionShouldAffectNoPriorities() throws Exception {
        Competition competition = newCompetition().withId(1L).build();

        List<Question> existingQuestions = newQuestion()
                .withId(1L, 2L, 3L)
                .withPriority(1, 2, 3)
                .withCompetition(competition)
                .build(3);

        Question lastQuestion = newQuestion()
                .withId(4L)
                .withPriority(3)
                .withCompetition(competition)
                .build();

        when(questionRepositoryMock.findByCompetitionIdAndSectionNameAndPriorityGreaterThanOrderByPriorityAsc(
                lastQuestion.getCompetition().getId(),
                APPLICATION_QUESTIONS.getName(),
                lastQuestion.getPriority()))
                .thenReturn(existingQuestions);

        service.reprioritiseAssessedQuestionsAfterDeletion(lastQuestion);

        List<Question> expectedQuestions = newQuestion()
                .withId(1L, 2L, 3L)
                .withPriority(1, 2, 3)
                .withCompetition(competition)
                .build(3);

        verify(questionRepositoryMock).save(refEq(expectedQuestions));
    }
}