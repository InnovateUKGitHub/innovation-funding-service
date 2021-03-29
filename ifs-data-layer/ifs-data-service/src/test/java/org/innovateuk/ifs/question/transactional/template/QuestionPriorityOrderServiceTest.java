package org.innovateuk.ifs.question.transactional.template;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.repository.QuestionRepository;
import org.innovateuk.ifs.form.resource.SectionType;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.form.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.form.builder.SectionBuilder.newSection;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.APPLICATION_DETAILS;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.*;


public class QuestionPriorityOrderServiceTest extends BaseServiceUnitTest<QuestionPriorityOrderService>{

    @Mock
    private QuestionRepository questionRepositoryMock;

    @Mock
    private QuestionNumberOrderService questionNumberOrderServiceMock;

    public QuestionPriorityOrderService supplyServiceUnderTest() {
        return new QuestionPriorityOrderService();
    }

    @Test
    public void prioritiseResearchCategoryQuestionAfterCreation() {
        Competition competition = newCompetition().build();

        Section section = newSection()
                .withSectionType(SectionType.PROJECT_DETAILS)
                .build();

        Question newlyCreatedQuestion = newQuestion()
                .withCompetition(competition)
                .withSection(section)
                .withPriority((Integer) null)
                .build();

        Question applicationDetailsQuestion = newQuestion()
                .withPriority(2)
                .build();

        List<Question> questions = newQuestion()
                .withPriority(3, 4, 5)
                .build(3);

        when(questionRepositoryMock.findFirstByCompetitionIdAndQuestionSetupType(competition.getId(),
                APPLICATION_DETAILS)).thenReturn(applicationDetailsQuestion);
        when(questionRepositoryMock.findByCompetitionIdAndSectionTypeAndPriorityGreaterThanOrderByPriorityAsc(
                competition.getId(), SectionType.PROJECT_DETAILS, applicationDetailsQuestion.getPriority())).thenReturn(
                questions);

        service.prioritiseResearchCategoryQuestionAfterCreation(newlyCreatedQuestion);

        assertEquals(applicationDetailsQuestion.getPriority() + 1, newlyCreatedQuestion.getPriority().intValue());

        List<Question> expectedQuestions = newQuestion()
                .withId(questions.get(0).getId(), questions.get(1).getId(), questions.get(2).getId())
                .withPriority(5, 5, 6)
                .build(3);

        verify(questionRepositoryMock).saveAll(refEq(expectedQuestions));
        verify(questionRepositoryMock).save(refEq(newlyCreatedQuestion));
    }

    @Test
    public void prioritiseAssessedQuestionAfterCreation() {
        Competition competition = newCompetition().build();

        List<Question> existingQuestions = newQuestion()
                .withId(1L, 2L, 3L, 4L)
                .withPriority(1, 2, 3, null)
                .withCompetition(competition)
                .build(4);

        Question lastPrioritizedQuestion = existingQuestions.get(2);
        Question newlyCreatedQuestion = existingQuestions.get(3);


        when(questionRepositoryMock.findFirstByCompetitionIdAndSectionTypeOrderByPriorityDesc(lastPrioritizedQuestion.getCompetition().getId(),
                SectionType.APPLICATION_QUESTIONS)).thenReturn(lastPrioritizedQuestion);

        service.prioritiseAssessedQuestionAfterCreation(newlyCreatedQuestion);

        Question expectedQuestion = newlyCreatedQuestion;
        expectedQuestion.setPriority(4);

        verify(questionNumberOrderServiceMock).updateAssessedQuestionsNumbers(expectedQuestion.getCompetition().getId());
        verify(questionRepositoryMock, times(1)).save(refEq(expectedQuestion));
    }

    @Test
    public void reprioritiseQuestionsAfterDeletion_deleteMiddleQuestionShouldAffectOnlySubsequentPriorities() {
        Competition competition = newCompetition().build();

        List<Question> existingQuestions = newQuestion()
                .withId(1L, 2L, 4L)
                .withPriority(1, 2, 4)
                .withCompetition(competition)
                .build(3);

        Section section = newSection()
                .withSectionType(SectionType.APPLICATION_QUESTIONS)
                .build();

        Question deletedQuestion = newQuestion()
                .withId(3L)
                .withPriority(3)
                .withCompetition(competition)
                .withSection(section)
                .build();

        when(questionRepositoryMock.findByCompetitionIdAndSectionTypeAndPriorityGreaterThanOrderByPriorityAsc(
                deletedQuestion.getCompetition().getId(),
                SectionType.APPLICATION_QUESTIONS,
                deletedQuestion.getPriority()))
                .thenReturn(existingQuestions);

        service.reprioritiseQuestionsAfterDeletion(deletedQuestion);

        List<Question> expectedQuestions = newQuestion()
                .withId(1L, 2L, 4L)
                .withPriority(1, 2, 3)
                .withCompetition(competition)
                .build(3);

        verify(questionRepositoryMock).saveAll(refEq(expectedQuestions));
    }

    @Test
    public void reprioritiseQuestionsAfterDeletion_deleteFirstQuestionShouldAffectAllPriorities() {
        Competition competition = newCompetition().build();

        List<Question> existingQuestions = newQuestion()
                .withId(2L, 3L, 4L)
                .withPriority(2, 3, 4)
                .withCompetition(competition)
                .build(3);

        Section section = newSection()
                .withSectionType(SectionType.APPLICATION_QUESTIONS)
                .build();

        Question deletedQuestion = newQuestion()
                .withId(1L)
                .withPriority(1)
                .withCompetition(competition)
                .withSection(section)
                .build();

        when(questionRepositoryMock.findByCompetitionIdAndSectionTypeAndPriorityGreaterThanOrderByPriorityAsc(
                deletedQuestion.getCompetition().getId(),
                SectionType.APPLICATION_QUESTIONS,
                deletedQuestion.getPriority()))
                .thenReturn(existingQuestions);

        service.reprioritiseQuestionsAfterDeletion(deletedQuestion);

        List<Question> expectedQuestions = newQuestion()
                .withId(2L, 3L, 4L)
                .withPriority(1, 2, 3)
                .withCompetition(competition)
                .build(3);

        verify(questionRepositoryMock).saveAll(refEq(expectedQuestions));
    }

    @Test
    public void reprioritiseQuestionsAfterDeletion_deleteLastQuestionShouldAffectNoPriorities() {
        Competition competition = newCompetition().build();

        List<Question> existingQuestions = newQuestion()
                .withId(1L, 2L, 3L)
                .withPriority(1, 2, 3)
                .withCompetition(competition)
                .build(3);

        Section section = newSection()
                .withSectionType(SectionType.APPLICATION_QUESTIONS)
                .build();

        Question lastQuestion = newQuestion()
                .withId(4L)
                .withPriority(3)
                .withCompetition(competition)
                .withSection(section)
                .build();

        when(questionRepositoryMock.findByCompetitionIdAndSectionTypeAndPriorityGreaterThanOrderByPriorityAsc(
                lastQuestion.getCompetition().getId(),
                SectionType.APPLICATION_QUESTIONS,
                lastQuestion.getPriority()))
                .thenReturn(existingQuestions);

        service.reprioritiseQuestionsAfterDeletion(lastQuestion);

        List<Question> expectedQuestions = newQuestion()
                .withId(1L, 2L, 3L)
                .withPriority(1, 2, 3)
                .withCompetition(competition)
                .build(3);

        verify(questionRepositoryMock).saveAll(refEq(expectedQuestions));
    }
}