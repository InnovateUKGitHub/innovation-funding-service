package org.innovateuk.ifs.question.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.repository.QuestionRepository;
import org.innovateuk.ifs.form.repository.SectionRepository;
import org.innovateuk.ifs.question.transactional.template.DefaultApplicationQuestionCreator;
import org.innovateuk.ifs.question.transactional.template.QuestionPriorityOrderService;
import org.innovateuk.ifs.question.transactional.template.QuestionTemplatePersistorImpl;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.form.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.form.builder.SectionBuilder.newSection;
import static org.innovateuk.ifs.setup.resource.QuestionSection.APPLICATION_QUESTIONS;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class QuestionSetupTemplateServiceImplTest extends BaseServiceUnitTest<QuestionSetupTemplateService> {

    @Mock
    private QuestionTemplatePersistorImpl questionTemplatePersistorServiceMock;

    @Mock
    private DefaultApplicationQuestionCreator defaultApplicationQuestionCreatorMock;

    @Mock
    private QuestionRepository questionRepositoryMock;

    @Mock
    private SectionRepository sectionRepositoryMock;

    @Mock
    private QuestionPriorityOrderService questionPriorityOrderServiceMock;

    @Override
    protected QuestionSetupTemplateService supplyServiceUnderTest() {
        return new QuestionSetupTemplateServiceImpl();
    }

    @Test
    public void deleteQuestionInCompetition_questionNotFoundShouldResultInServiceFailure() {
        final long questionId = 1L;

        when(questionRepositoryMock.findFirstById(questionId)).thenReturn(null);
        ServiceResult<Void> resultAssessedQuestion = service.deleteQuestionInCompetition(questionId);
        assertTrue(resultAssessedQuestion.isFailure());
    }

    @Test
    public void deleteQuestionInCompetition_questionWithoutCompetitionShouldResultInServiceFailure() {
        Question question = newQuestion().build();

        when(questionRepositoryMock.findFirstById(question.getId())).thenReturn(question);
        ServiceResult<Void> resultAssessedQuestion = service.deleteQuestionInCompetition(question.getId());
        assertTrue(resultAssessedQuestion.isFailure());
    }

    @Test
    public void deleteQuestionInCompetition_competitionNotInSetupOrReadyStateShouldResultInFailure() {
        Competition competition = newCompetition().withCompetitionStatus(CompetitionStatus.OPEN).build();
        Question question = newQuestion().withCompetition(competition).build();


        when(questionRepositoryMock.findFirstById(question.getId())).thenReturn(question);
        ServiceResult<Void> resultAssessedQuestion = service.deleteQuestionInCompetition(question.getId());
        assertTrue(resultAssessedQuestion.isFailure());
    }

    @Test
    public void deleteAssessedQuestionInCompetition_questionSectionTotalQuestionIsOneOrLessShouldResultInFailure() {
        Competition readyToOpenCompetition = newCompetition().withCompetitionStatus(CompetitionStatus.READY_TO_OPEN).build();
        Section section = newSection().withName(APPLICATION_QUESTIONS.getName()).build();
        Question question = newQuestion().withCompetition(readyToOpenCompetition).withSection(section).build();

        when(questionRepositoryMock.findFirstById(question.getId())).thenReturn(question);
        when(questionRepositoryMock.countByCompetitionId(readyToOpenCompetition.getId())).thenReturn(1L);
        ServiceResult<Void> resultAssessedQuestion = service.deleteQuestionInCompetition(question.getId());
        assertTrue(resultAssessedQuestion.isFailure());
    }

    @Test
    public void deleteQuestionInCompetition_invalidSectionShouldResultInFailure() {
        Competition readyToOpenCompetition = newCompetition().withCompetitionStatus(CompetitionStatus.READY_TO_OPEN).build();
        Question question = newQuestion().withCompetition(readyToOpenCompetition).build();

        ServiceResult<Void> resultNonExisting = service.deleteQuestionInCompetition(question.getId());
        assertTrue(resultNonExisting.isFailure());

        ServiceResult<Void> resultInvalid = service.deleteQuestionInCompetition(question.getId());
        assertTrue(resultInvalid.isFailure());
    }

    @Test
    public void addDefaultAssessedQuestionToCompetition_competitionIsNullShouldResultInFailure() {
        ServiceResult<Question> result = service.addDefaultAssessedQuestionToCompetition(null);

        assertTrue(result.isFailure());
    }

    @Test
    public void addDefaultAssessedQuestionToCompetition_competitionIsNotInReadyOrSetupStateShouldResultInFailure() {
        Competition competitionInWrongState = newCompetition().withCompetitionStatus(CompetitionStatus.OPEN).build();

        ServiceResult<Question> result = service.addDefaultAssessedQuestionToCompetition(competitionInWrongState);

        assertTrue(result.isFailure());
    }

    @Test
    public void addDefaultAssessedQuestionToCompetition_sectionCannotBeFoundShouldResultInServiceFailure() {
        Competition competitionInWrongState = newCompetition().withCompetitionStatus(CompetitionStatus.READY_TO_OPEN).build();

        when(sectionRepositoryMock.findFirstByCompetitionIdAndName(competitionInWrongState.getId(), APPLICATION_QUESTIONS.getName())).thenReturn(null);

        ServiceResult<Question> result = service.addDefaultAssessedQuestionToCompetition(competitionInWrongState);

        assertTrue(result.isFailure());
    }

    @Test
    public void addDefaultAssessedQuestionToCompetition_addingQuestionShouldResultInPersistingAndReprioritizingQuestions() {
        Competition competition = newCompetition().withCompetitionStatus(CompetitionStatus.READY_TO_OPEN).build();
        Section section = newSection().build();
        Question createdQuestion = newQuestion().build();

        when(sectionRepositoryMock.findFirstByCompetitionIdAndName(competition.getId(), APPLICATION_QUESTIONS.getName())).thenReturn(section);
        when(defaultApplicationQuestionCreatorMock.buildQuestion(competition)).thenReturn(createdQuestion);
        when(questionTemplatePersistorServiceMock.persistByEntity(any())).thenReturn(asList(createdQuestion));

        ServiceResult<Question> result = service.addDefaultAssessedQuestionToCompetition(competition);

        assertTrue(result.isSuccess());

        Question expectedQuestion = createdQuestion;
        expectedQuestion.setSection(section);
        expectedQuestion.setCompetition(competition);

        verify(questionTemplatePersistorServiceMock).persistByEntity(asList(createdQuestion));
        verify(questionPriorityOrderServiceMock).prioritiseAssessedQuestionAfterCreation(isA(Question.class));
    }
}
