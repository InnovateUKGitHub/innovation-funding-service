package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionType;
import org.innovateuk.ifs.competition.repository.CompetitionTypeRepository;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.transactional.template.CompetitionTemplatePersistorImpl;
import org.innovateuk.ifs.competition.transactional.template.DefaultApplicationQuestionCreator;
import org.innovateuk.ifs.competition.transactional.template.QuestionTemplatePersistorImpl;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.application.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.application.builder.SectionBuilder.newSection;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NOT_EDITABLE;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.*;

public class CompetitionSetupTemplateServiceImplTest extends BaseServiceUnitTest<CompetitionSetupTemplateService>{
    public CompetitionSetupTemplateService supplyServiceUnderTest() {
        return new CompetitionSetupTemplateServiceImpl();
    }

    private static String ASSESSED_QUESTIONS_SECTION_NAME = "Application questions";

    @Mock
    private CompetitionTypeRepository competitionTypeRepositoryMock;

    @Mock
    private CompetitionTemplatePersistorImpl competitionTemplatePersistorMock;

    @Mock
    private QuestionTemplatePersistorImpl questionTemplatePersistorServiceMock;

    @Mock
    private DefaultApplicationQuestionCreator defaultApplicationQuestionCreatorMock;

    @Test
    public void testInitializeCompetitionByCompetitionTemplate_competitionTypeCantBeFoundShouldResultException() throws Exception {
        Competition competitionTemplate = newCompetition().withId(2L).build();
        CompetitionType competitionType = newCompetitionType().withTemplate(competitionTemplate).withId(1L).build();
        Competition competition = newCompetition().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).withId(3L).build();

        when(competitionTypeRepositoryMock.findOne(competitionType.getId())).thenReturn(null);
        when(competitionRepositoryMock.findById(competition.getId())).thenReturn(competition);
        when(assessorCountOptionRepositoryMock.findByCompetitionTypeIdAndDefaultOptionTrue(competitionType.getId())).thenReturn(Optional.empty());

        ServiceResult<Competition> result = service.initializeCompetitionByCompetitionTemplate(competition.getId(), competitionType.getId());

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(COMPETITION_NOT_EDITABLE));

        verifyZeroInteractions(competitionTemplatePersistorMock);
    }

    @Test
    public void testInitializeCompetitionByCompetitionTemplate_competitionCantBeFoundShouldResultInServiceFailure() throws Exception {
        Competition competitionTemplate = newCompetition().withId(2L).build();
        CompetitionType competitionType = newCompetitionType().withTemplate(competitionTemplate).withId(1L).build();
        Competition competition = newCompetition().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).withId(3L).build();

        when(competitionTypeRepositoryMock.findOne(competitionType.getId())).thenReturn(competitionType);
        when(competitionRepositoryMock.findById(competition.getId())).thenReturn(null);
        when(assessorCountOptionRepositoryMock.findByCompetitionTypeIdAndDefaultOptionTrue(competitionType.getId())).thenReturn(Optional.empty());

        ServiceResult<Competition> result = service.initializeCompetitionByCompetitionTemplate(competition.getId(), competitionType.getId());

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(COMPETITION_NOT_EDITABLE));

        verifyZeroInteractions(competitionTemplatePersistorMock);
    }

    @Test
    public void testInitializeCompetitionByCompetitionTemplate_competitionNotInCompetitionSetupShouldResultInServiceFailure() throws Exception {
        Competition competitionTemplate = newCompetition().withId(2L).build();
        CompetitionType competitionType = newCompetitionType().withTemplate(competitionTemplate).withId(1L).build();
        Competition competition = newCompetition().withCompetitionStatus(CompetitionStatus.READY_TO_OPEN).withId(3L).build();

        when(competitionTypeRepositoryMock.findOne(competitionType.getId())).thenReturn(competitionType);
        when(competitionRepositoryMock.findById(competition.getId())).thenReturn(competition);
        when(assessorCountOptionRepositoryMock.findByCompetitionTypeIdAndDefaultOptionTrue(competitionType.getId())).thenReturn(Optional.empty());

        ServiceResult<Competition> result = service.initializeCompetitionByCompetitionTemplate(competition.getId(), competitionType.getId());

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(COMPETITION_NOT_EDITABLE));

        verifyZeroInteractions(competitionTemplatePersistorMock);
    }

    @Test
    public void testInitializeCompetitionByCompetitionTemplate_templateCantBeFoundShouldResultInServiceFailure() throws Exception {
        CompetitionType competitionType = newCompetitionType().withId(1L).build();
        Competition competition = newCompetition().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).withId(3L).build();

        when(competitionTypeRepositoryMock.findOne(competitionType.getId())).thenReturn(competitionType);
        when(competitionRepositoryMock.findById(competition.getId())).thenReturn(competition);
        when(assessorCountOptionRepositoryMock.findByCompetitionTypeIdAndDefaultOptionTrue(competitionType.getId())).thenReturn(Optional.empty());

        ServiceResult<Competition> result = service.initializeCompetitionByCompetitionTemplate(competition.getId(), competitionType.getId());

        assertTrue(result.isFailure());

        verifyZeroInteractions(competitionTemplatePersistorMock);
    }

    @Test
    public void testInitializeCompetitionByCompetitionTemplate_competitionShouldBeCleanedAndPersistedWithTemplateSections() throws Exception {
        List<Section> templateSections = newSection().withId(1L, 2L, 3L).build(3);
        Competition competitionTemplate = newCompetition().withId(2L).withSections(templateSections).build();
        CompetitionType competitionType = newCompetitionType().withTemplate(competitionTemplate).withId(1L).build();
        Competition competition = newCompetition().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).withId(3L).build();
        Competition expectedResult = newCompetition().withId(4L).build();

        when(competitionTypeRepositoryMock.findOne(competitionType.getId())).thenReturn(competitionType);
        when(competitionRepositoryMock.findById(competition.getId())).thenReturn(competition);
        when(competitionTemplatePersistorMock.persistByEntity(competition)).thenReturn(expectedResult);
        when(assessorCountOptionRepositoryMock.findByCompetitionTypeIdAndDefaultOptionTrue(competitionType.getId())).thenReturn(Optional.empty());

        ServiceResult<Competition> result = service.initializeCompetitionByCompetitionTemplate(competition.getId(), competitionType.getId());

        assertTrue(result.isSuccess());
        assertEquals(result.getSuccessObject().getId(), expectedResult.getId());

        Competition competitionWithTemplateSectionsAttached = competition;

        InOrder inOrder = inOrder(competitionTemplatePersistorMock);
        inOrder.verify(competitionTemplatePersistorMock).cleanByEntityId(competition.getId());
        inOrder.verify(competitionTemplatePersistorMock).persistByEntity(refEq(competitionWithTemplateSectionsAttached));
    }

    @Test
    public void testInitializeCompetitionByCompetitionTemplate_defaultAssessorPayAndCountShouldBeSetOnCompetition() throws Exception {
        List<Section> templateSections = newSection().withId(1L, 2L, 3L).build(3);
        Competition competitionTemplate = newCompetition().withId(2L).withSections(templateSections).build();
        CompetitionType competitionType = newCompetitionType().withTemplate(competitionTemplate).withId(1L).build();
        Competition competition = newCompetition().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).withId(3L).build();
        Competition expectedResult = newCompetition().withId(4L).build();

        when(competitionTypeRepositoryMock.findOne(competitionType.getId())).thenReturn(competitionType);
        when(competitionRepositoryMock.findById(competition.getId())).thenReturn(competition);
        when(competitionTemplatePersistorMock.persistByEntity(competition)).thenReturn(expectedResult);
        when(assessorCountOptionRepositoryMock.findByCompetitionTypeIdAndDefaultOptionTrue(competitionType.getId())).thenReturn(Optional.empty());

        ServiceResult<Competition> result = service.initializeCompetitionByCompetitionTemplate(competition.getId(), competitionType.getId());

        assertTrue(result.isSuccess());
        assertEquals(result.getSuccessObject().getId(), expectedResult.getId());

        Competition competitionWithTemplateSectionsAttached = competition;

        InOrder inOrder = inOrder(competitionTemplatePersistorMock);
        inOrder.verify(competitionTemplatePersistorMock).cleanByEntityId(competition.getId());
        inOrder.verify(competitionTemplatePersistorMock).persistByEntity(refEq(competitionWithTemplateSectionsAttached));
    }

    @Test
    public void testDeleteAssessedQuestionInCompetition_questionNotFoundShouldResultInServiceFailure() {
        Long questionId = 1L;

        when(questionRepositoryMock.findFirstByIdAndSectionName(questionId, ASSESSED_QUESTIONS_SECTION_NAME)).thenReturn(null);

        ServiceResult<Void> result = service.deleteAssessedQuestionInCompetition(questionId);

        assertTrue(result.isFailure());
    }

    @Test
    public void testDeleteAssessedQuestionInCompetition_questionWithoutCompetitionShouldResultInServiceFailure() {
        Question question = newQuestion().withId(1L).build();

        when(questionRepositoryMock.findFirstByIdAndSectionName(question.getId(), ASSESSED_QUESTIONS_SECTION_NAME)).thenReturn(question);

        ServiceResult<Void> result = service.deleteAssessedQuestionInCompetition(question.getId());

        assertTrue(result.isFailure());
    }

    @Test
    public void testDeleteAssessedQuestionInCompetition_competitionNotInSetupOrReadyStateShouldResultInFailure() {
        Question question = newQuestion().withCompetition(newCompetition().withCompetitionStatus(CompetitionStatus.OPEN).build()).withId(1L).build();

        when(questionRepositoryMock.findFirstByIdAndSectionName(question.getId(), ASSESSED_QUESTIONS_SECTION_NAME)).thenReturn(question);

        ServiceResult<Void> result = service.deleteAssessedQuestionInCompetition(question.getId());

        assertTrue(result.isFailure());
    }

    @Test
    public void testDeleteAssessedQuestionInCompetition_questionSectionTotalQuestionIsOneOrLessShouldResultInFailure() {
        Competition readyToOpenCompetition = newCompetition().withCompetitionStatus(CompetitionStatus.READY_TO_OPEN).build();

        Question question = newQuestion().withCompetition(readyToOpenCompetition).withId(1L).build();

        when(questionRepositoryMock.findFirstByIdAndSectionName(question.getId(), ASSESSED_QUESTIONS_SECTION_NAME)).thenReturn(question);
        when(questionRepositoryMock.countByCompetitionIdAndSectionName(readyToOpenCompetition.getId(), ASSESSED_QUESTIONS_SECTION_NAME)).thenReturn(1L);

        ServiceResult<Void> result = service.deleteAssessedQuestionInCompetition(question.getId());

        assertTrue(result.isFailure());
    }

    @Test
    public void testDeleteAssessedQuestionInCompetition_validQuestionShouldResultInDeleteAndReprioritizeCallAndSuccess() {
        Competition readyToOpenCompetition = newCompetition().withCompetitionStatus(CompetitionStatus.READY_TO_OPEN).build();

        Question question = newQuestion().withCompetition(readyToOpenCompetition).withId(1L).build();

        when(questionRepositoryMock.findFirstByIdAndSectionName(question.getId(), ASSESSED_QUESTIONS_SECTION_NAME)).thenReturn(question);
        when(questionRepositoryMock.countByCompetitionIdAndSectionName(readyToOpenCompetition.getId(), ASSESSED_QUESTIONS_SECTION_NAME)).thenReturn(2L);

        ServiceResult<Void> result = service.deleteAssessedQuestionInCompetition(question.getId());

        assertTrue(result.isSuccess());

        verify(questionTemplatePersistorServiceMock).deleteEntityById(question.getId());
        verify(questionPriorityOrderServiceMock).reprioritiseAssessedQuestionsAfterDeletion(question);
    }


    @Test
    public void testAddDefaultAssessedQuestionToCompetition_competitionIsNullShouldResultInFailure() throws Exception {
        ServiceResult<Question> result = service.addDefaultAssessedQuestionToCompetition(null);

        assertTrue(result.isFailure());
    }

    @Test
    public void testAddDefaultAssessedQuestionToCompetition_competitionIsNotInReadyOrSetupStateShouldResultInFailure() throws Exception {
        Competition competitionInWrongState = newCompetition().withCompetitionStatus(CompetitionStatus.OPEN).build();

        ServiceResult<Question> result = service.addDefaultAssessedQuestionToCompetition(competitionInWrongState);

        assertTrue(result.isFailure());
    }

    @Test
    public void testAddDefaultAssessedQuestionToCompetition_sectionCannotBeFoundShouldResultInServiceFailure() throws Exception {
        Competition competitionInWrongState = newCompetition().withCompetitionStatus(CompetitionStatus.READY_TO_OPEN).build();

        when(sectionRepositoryMock.findFirstByCompetitionIdAndName(competitionInWrongState.getId(), ASSESSED_QUESTIONS_SECTION_NAME)).thenReturn(null);

        ServiceResult<Question> result = service.addDefaultAssessedQuestionToCompetition(competitionInWrongState);

        assertTrue(result.isFailure());
    }

    @Test
    public void testAddDefaultAssessedQuestionToCompetition_addingQuestionShouldResultInPersistingAndReprioritizingQuestions() throws Exception {
        Competition competition = newCompetition().withCompetitionStatus(CompetitionStatus.READY_TO_OPEN).build();
        Section section = newSection().build();
        Question createdQuestion = newQuestion().build();

        when(sectionRepositoryMock.findFirstByCompetitionIdAndName(competition.getId(), ASSESSED_QUESTIONS_SECTION_NAME)).thenReturn(section);
        when(defaultApplicationQuestionCreatorMock.buildQuestion(competition)).thenReturn(createdQuestion);
        when(questionTemplatePersistorServiceMock.persistByEntity(any())).thenReturn(Arrays.asList(createdQuestion));

        ServiceResult<Question> result = service.addDefaultAssessedQuestionToCompetition(competition);

        assertTrue(result.isSuccess());

        Question expectedQuestion = createdQuestion;
        expectedQuestion.setSection(section);
        expectedQuestion.setCompetition(competition);

        verify(questionTemplatePersistorServiceMock).persistByEntity(Arrays.asList(createdQuestion));
        verify(questionPriorityOrderServiceMock).prioritiseAssessedQuestionAfterCreation(isA(Question.class));
    }
}