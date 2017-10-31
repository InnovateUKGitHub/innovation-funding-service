package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.competition.domain.Competition;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.application.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.*;


public class QuestionPriorityServiceTest extends BaseServiceUnitTest<QuestionReprioritisationService>{

    private static String ASSESSED_QUESTIONS_SECTION_NAME = "Application questions";

    public QuestionReprioritisationService supplyServiceUnderTest() {
        return new QuestionReprioritisationService();
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


        when(questionRepositoryMock.findFirstByCompetitionIdAndSectionNameOrderByPriorityDesc(lastPrioritizedQuestion.getCompetition().getId(), ASSESSED_QUESTIONS_SECTION_NAME)).thenReturn(lastPrioritizedQuestion);

        service.prioritiseAssessedQuestionAfterCreation(newlyCreatedQuestion);

        Question expectedQuestion = newlyCreatedQuestion;
        expectedQuestion.setPriority(4);

        verify(questionRenumberingService).updateAssessedQuestionsNumbers(expectedQuestion.getCompetition().getId());
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
                ASSESSED_QUESTIONS_SECTION_NAME,
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
                ASSESSED_QUESTIONS_SECTION_NAME,
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
                ASSESSED_QUESTIONS_SECTION_NAME,
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