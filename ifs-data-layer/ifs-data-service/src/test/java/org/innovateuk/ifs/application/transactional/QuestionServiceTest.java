package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.QuestionType;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.transactional.UserService;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.application.builder.SectionBuilder.newSection;
import static org.innovateuk.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.ASSIGNEE_SHOULD_BE_APPLICANT;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class QuestionServiceTest extends BaseUnitTestMocksTest {

    @InjectMocks
    protected QuestionService questionService = new QuestionServiceImpl();

    @Mock
    private SectionService sectionService;

    @Mock
    private UserService userService;

    @Test
    public void getNextQuestionTest() throws Exception {
        Question question = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 1).build();
        Question nextQuestion = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 2).build();
        QuestionResource nextQuestionResource = newQuestionResource().withCompetitionAndSectionAndPriority(newCompetitionResource().build(), newSectionResource().build(), 2).build();

        when(questionRepositoryMock.findOne(question.getId())).thenReturn(question);
        when(questionRepositoryMock.findFirstByCompetitionIdAndSectionIdAndPriorityGreaterThanOrderByPriorityAsc(
                question.getCompetition().getId(), question.getSection().getId(), question.getPriority()))
                .thenReturn(nextQuestion);
        when(questionMapperMock.mapToResource(nextQuestion)).thenReturn(nextQuestionResource);

        // Method under test
        assertEquals(nextQuestionResource, questionService.getNextQuestion(question.getId()).getSuccessObject());
    }

    @Test
    public void getPreviousQuestionTest() throws Exception {
        Question question = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 2).build();
        Question previousQuestion = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 1).build();
        QuestionResource previousQuestionResource = newQuestionResource().withCompetitionAndSectionAndPriority(newCompetitionResource().build(), newSectionResource().build(), 1).build();

        when(questionRepositoryMock.findOne(question.getId())).thenReturn(question);
        when(questionRepositoryMock.findFirstByCompetitionIdAndSectionIdAndPriorityLessThanOrderByPriorityDesc(
                question.getCompetition().getId(), question.getSection().getId(), question.getPriority()))
                .thenReturn(previousQuestion);
        when(questionMapperMock.mapToResource(previousQuestion)).thenReturn(previousQuestionResource);

        // Method under test
        assertEquals(previousQuestionResource, questionService.getPreviousQuestion(question.getId()).getSuccessObject());
    }

    @Test
    public void getNextQuestionFromOtherSectionTest() throws Exception {
        Section nextSection = newSection().build();
        SectionResource nextSectionResource = newSectionResource().build();
        Question question = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 1).build();
        Question nextQuestion = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), nextSection, 2).build();
        QuestionResource nextQuestionResource = newQuestionResource().withCompetitionAndSectionAndPriority(newCompetitionResource().build(), nextSectionResource, 2).build();

        when(questionRepositoryMock.findOne(question.getId())).thenReturn(question);
        when(sectionService.getNextSection(any(SectionResource.class))).thenReturn(serviceSuccess(nextSectionResource));
        when(questionRepositoryMock.findFirstByCompetitionIdAndSectionIdAndPriorityGreaterThanOrderByPriorityAsc(
                question.getCompetition().getId(), question.getSection().getId(), question.getPriority())).thenReturn(nextQuestion);
        when(questionMapperMock.mapToResource(nextQuestion)).thenReturn(nextQuestionResource);

        // Method under test
        assertEquals(nextQuestionResource, questionService.getNextQuestion(question.getId()).getSuccessObject());
    }

    @Test
    public void getPreviousQuestionFromOtherSectionTest() throws Exception {
        Section previousSection = newSection().build();
        SectionResource previousSectionResource = newSectionResource().build();
        Competition competition = newCompetition().build();
        CompetitionResource competitionResource = newCompetitionResource().build();
        Question question = newQuestion().withCompetitionAndSectionAndPriority(competition, newSection().build(), 2).build();
        Question previousQuestion = newQuestion().withCompetitionAndSectionAndPriority(competition, previousSection, 1).build();
        QuestionResource previousQuestionResource = newQuestionResource().withCompetitionAndSectionAndPriority(competitionResource, previousSectionResource, 1).build();

        when(questionRepositoryMock.findOne(question.getId())).thenReturn(question);
        when(sectionService.getPreviousSection(any(SectionResource.class)))
                .thenReturn(serviceSuccess(previousSectionResource));
        when(questionRepositoryMock.findFirstByCompetitionIdAndSectionIdOrderByPriorityDesc(
                question.getCompetition().getId(), previousQuestion.getSection().getId()))
                .thenReturn(previousQuestion);
        when(questionRepositoryMock.findFirstByCompetitionIdAndSectionIdAndPriorityLessThanOrderByPriorityDesc(
                question.getCompetition().getId(), question.getSection().getId(), question.getPriority()))
                .thenReturn(previousQuestion);
        when(questionMapperMock.mapToResource(previousQuestion)).thenReturn(previousQuestionResource);

        // Method under test
        assertEquals(previousQuestionResource, questionService.getPreviousQuestion(question.getId()).getSuccessObject());

    }

    @Test
    public void getPreviousQuestionBySectionTest() throws Exception {
        Section currentSection = newSection().withCompetitionAndPriorityAndParent(newCompetition().build(), 1, newSection().build()).build();
        SectionResource currentSectionResource = newSectionResource().withCompetitionAndPriorityAndParent(newCompetition().build().getId(), 1, newSection().build().getId()).build();
        Question previousSectionQuestion = newQuestion().build();
        QuestionResource previousSectionQuestionResource = newQuestionResource().build();
        SectionResource previousSectionResource = newSectionResource().withQuestions(Arrays.asList(previousSectionQuestion.getId())).build();
        when(sectionService.getById(currentSection.getId())).thenReturn(serviceSuccess(currentSectionResource));
        when(sectionService.getPreviousSection(currentSectionResource)).thenReturn(serviceSuccess(previousSectionResource));
        when(questionRepositoryMock.findOne(anyLong())).thenReturn(previousSectionQuestion);
        // Method under test
        when(questionMapperMock.mapToResource(previousSectionQuestion)).thenReturn(previousSectionQuestionResource);

        assertEquals(previousSectionQuestionResource, questionService.getPreviousQuestionBySection(currentSection.getId()).getSuccessObject());
    }

    @Test
    public void getQuestionsBySectionIdAndTypeTest() {

        Question child1CostQuestion = newQuestion().withQuestionType(QuestionType.COST).build();
        Question child1OtherQuestion = newQuestion().withQuestionType(QuestionType.GENERAL).build();
        Section childSection1 = newSection().withQuestions(asList(child1CostQuestion, child1OtherQuestion)).build();

        Question child2CostQuestion = newQuestion().withQuestionType(QuestionType.COST).build();
        Question child2OtherQuestion = newQuestion().withQuestionType(QuestionType.GENERAL).build();
        Section childSection2 = newSection().withQuestions(asList(child2CostQuestion, child2OtherQuestion)).build();

        Question parentCostQuestion = newQuestion().withQuestionType(QuestionType.COST).build();
        Question parentOtherQuestion = newQuestion().withQuestionType(QuestionType.GENERAL).build();

        Section parentSection = newSection()
                .withQuestions(asList(parentCostQuestion, parentOtherQuestion))
                .withChildSections(asList(childSection1, childSection2))
                .build();

        when(sectionRepositoryMock.findOne(1L)).thenReturn(parentSection);

        QuestionResource questionResource1 = newQuestionResource().build();
        QuestionResource questionResource2 = newQuestionResource().build();
        QuestionResource questionResource3 = newQuestionResource().build();

        when(questionMapperMock.mapToResource(child1CostQuestion)).thenReturn(questionResource1);
        when(questionMapperMock.mapToResource(child2CostQuestion)).thenReturn(questionResource2);
        when(questionMapperMock.mapToResource(parentCostQuestion)).thenReturn(questionResource3);


        ServiceResult<List<QuestionResource>> result = questionService.getQuestionsBySectionIdAndType(1L, QuestionType.COST);

        assertTrue(result.isSuccess());
        assertEquals(3, result.getSuccessObject().size());
        assertTrue(result.getSuccessObject().contains(questionResource1));
        assertTrue(result.getSuccessObject().contains(questionResource2));
        assertTrue(result.getSuccessObject().contains(questionResource3));
    }

    @Test
    public void saveQuestionResource() throws Exception {
        QuestionResource questionResource = newQuestionResource().build();
        Question question = newQuestion().build();

        when(questionMapperMock.mapToDomain(questionResource)).thenReturn(question);
        when(questionMapperMock.mapToResource(question)).thenReturn(questionResource);
        when(questionRepositoryMock.save(question)).thenReturn(question);

        ServiceResult<QuestionResource> result = questionService.save(questionResource);

        assertTrue(result.isSuccess());
        assertEquals(questionResource, result.getSuccessObject());
    }

    @Test
    public void assignTest() throws Exception {
        final Long applicationId = 1232L;
        final Long questionId = 2228L;
        final Long assigneeId = 51234L;
        final Long assignedById = 72834L;
        QuestionApplicationCompositeId questionApplicationCompositeId = new QuestionApplicationCompositeId(questionId, applicationId);

        when(questionRepositoryMock.findOne(questionId)).thenReturn(newQuestion().build());
        when(processRoleRepositoryMock.findOne(assigneeId)).thenReturn(newProcessRole().withUser(newUser().withId(assigneeId).build()).withApplication(newApplication().withId(applicationId).build()).build());
        when(processRoleRepositoryMock.findOne(assignedById)).thenReturn(newProcessRole().build());
        Competition competitionMock = mock(Competition.class);
        when(competitionMock.getCompetitionStatus()).thenReturn(CompetitionStatus.OPEN);
        Application application = newApplication().withCompetition(competitionMock).build();
        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
        when(userService.findAssignableUsers(applicationId)).thenReturn(serviceSuccess(new HashSet(newUserResource().withId(assigneeId).build(1))));

        ServiceResult<Void> result = questionService.assign(questionApplicationCompositeId, assigneeId, assignedById);

        assertTrue(result.isSuccess());

        Long differentApplicationId = 1233L;
        when(processRoleRepositoryMock.findOne(assigneeId))
                .thenReturn(newProcessRole().withUser(newUser().withId(2L).build()).withApplication(newApplication().withId(differentApplicationId).build()).build());

        when(userService.findAssignableUsers(applicationId)).thenReturn(serviceSuccess(new HashSet(newUserResource().withId(1L).build(1))));

        ServiceResult<Void> resultTwo = questionService.assign(questionApplicationCompositeId, assigneeId, assignedById);

        assertTrue(resultTwo.isFailure());
        assertEquals(ASSIGNEE_SHOULD_BE_APPLICANT.getErrorKey(), resultTwo.getFailure().getErrors().get(0).getErrorKey());
    }

    @Test
    public void getQuestionByIdAndAssessmentId() throws Exception {
        Long questionId = 1L;
        Long assessmentId = 2L;

        Competition competition = newCompetition()
                .build();

        Application application = newApplication()
                .withCompetition(competition)
                .build();

        Assessment assessment = newAssessment()
                .withApplication(application)
                .build();

        Question question = newQuestion()
                .withCompetition(competition)
                .build();

        QuestionResource questionResource = newQuestionResource().build();

        when(questionRepositoryMock.findOne(questionId)).thenReturn(question);
        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(questionMapperMock.mapToResource(question)).thenReturn(questionResource);

        ServiceResult<QuestionResource> result = questionService.getQuestionByIdAndAssessmentId(questionId, assessmentId);

        assertTrue(result.isSuccess());
        assertEquals(questionResource, result.getSuccessObject());

        InOrder inOrder = inOrder(assessmentRepositoryMock, questionRepositoryMock, questionMapperMock);
        inOrder.verify(assessmentRepositoryMock).findOne(assessmentId);
        inOrder.verify(questionRepositoryMock).findOne(questionId);
        inOrder.verify(questionMapperMock).mapToResource(question);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getQuestionByIdAndAssessmentId_assessmentNotFound() throws Exception {
        Long questionId = 1L;
        Long assessmentId = 2L;

        ServiceResult<QuestionResource> result = questionService.getQuestionByIdAndAssessmentId(questionId, assessmentId);
        assertTrue(result.getFailure().is(notFoundError(Assessment.class, assessmentId)));

        InOrder inOrder = inOrder(assessmentRepositoryMock);
        inOrder.verify(assessmentRepositoryMock).findOne(assessmentId);
        inOrder.verifyNoMoreInteractions();

        verifyZeroInteractions(questionRepositoryMock);
        verifyZeroInteractions(questionMapperMock);
    }

    @Test
    public void getQuestionByIdAndAssessmentId_questionNotFound() throws Exception {
        Long questionId = 1L;
        Long assessmentId = 2L;

        Competition competition = newCompetition()
                .build();

        Application application = newApplication()
                .withCompetition(competition)
                .build();

        Assessment assessment = newAssessment()
                .withApplication(application)
                .build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);

        ServiceResult<QuestionResource> result = questionService.getQuestionByIdAndAssessmentId(questionId, assessmentId);
        assertTrue(result.getFailure().is(notFoundError(Question.class, questionId)));

        InOrder inOrder = inOrder(assessmentRepositoryMock, questionRepositoryMock);
        inOrder.verify(assessmentRepositoryMock).findOne(assessmentId);
        inOrder.verify(questionRepositoryMock).findOne(questionId);
        inOrder.verifyNoMoreInteractions();

        verifyZeroInteractions(questionMapperMock);
    }

    @Test
    public void getQuestionByIdAndAssessmentId_questionNotInTargetOfAssessment() throws Exception {
        Long questionId = 1L;
        Long assessmentId = 2L;

        Competition competition = newCompetition()
                .build();

        Competition otherCompetition = newCompetition()
                .build();

        Application application = newApplication()
                .withCompetition(competition)
                .build();

        Assessment assessment = newAssessment()
                .withApplication(application)
                .build();

        Question question = newQuestion()
                .withCompetition(otherCompetition)
                .build();

        when(questionRepositoryMock.findOne(questionId)).thenReturn(question);
        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);

        ServiceResult<QuestionResource> result = questionService.getQuestionByIdAndAssessmentId(questionId, assessmentId);
        assertTrue(result.getFailure().is(notFoundError(Question.class, questionId, assessmentId)));

        InOrder inOrder = inOrder(assessmentRepositoryMock, questionRepositoryMock);
        inOrder.verify(assessmentRepositoryMock).findOne(assessmentId);
        inOrder.verify(questionRepositoryMock).findOne(questionId);
        inOrder.verifyNoMoreInteractions();

        verifyZeroInteractions(questionMapperMock);
    }

    @Test
    public void getQuestionsByAssessmentId() {
        Long assessmentId = 1L;
        Long competitionId = 2L;

        Competition competition = newCompetition()
                .with(id(competitionId))
                .build();

        Application application = newApplication()
                .withCompetition(competition)
                .build();

        ProcessRole processRole = newProcessRole()
                .withApplication(application)
                .build();

        Assessment assessment = newAssessment()
                .with(id(assessmentId))
                .withParticipant(processRole)
                .build();

        List<Question> questionsForSection1 = newQuestion().build(2);
        List<QuestionResource> questionResourcesForSection1 = newQuestionResource().build(2);

        List<Question> questionsForSection2 = newQuestion().build(2);
        List<QuestionResource> questionResourcesForSection2 = newQuestionResource().build(2);

        List<Section> sections = newSection()
                .withQuestions(questionsForSection1, questionsForSection2)
                .withDisplayInAssessmentApplicationSummary(true)
                .build(2);

        List<SectionResource> sectionsResources = newSectionResource().build(2);

        when(applicationRepositoryMock.findOne(application.getId())).thenReturn(application);
        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(sectionService.getByCompetitionIdVisibleForAssessment(competitionId)).thenReturn(serviceSuccess(sectionsResources));
        when(sectionMapperMock.mapToDomain(same(sectionsResources.get(0)))).thenReturn(sections.get(0));
        when(sectionMapperMock.mapToDomain(same(sectionsResources.get(1)))).thenReturn(sections.get(1));
        when(questionMapperMock.mapToResource(same(questionsForSection1.get(0)))).thenReturn(questionResourcesForSection1.get(0));
        when(questionMapperMock.mapToResource(same(questionsForSection1.get(1)))).thenReturn(questionResourcesForSection1.get(1));
        when(questionMapperMock.mapToResource(same(questionsForSection2.get(0)))).thenReturn(questionResourcesForSection2.get(0));
        when(questionMapperMock.mapToResource(same(questionsForSection2.get(1)))).thenReturn(questionResourcesForSection2.get(1));

        ServiceResult<List<QuestionResource>> result = questionService.getQuestionsByAssessmentId(assessmentId);

        List<QuestionResource> expectedQuestions = concat(questionResourcesForSection1.stream(), questionResourcesForSection2.stream()).collect(toList());

        assertTrue(result.isSuccess());
        assertEquals(expectedQuestions, result.getSuccessObject());
    }

    @Test
    public void testGetQuestionByCompetitionIdAndFormInputTypeSuccess() {
        long competitionId = 1L;

        Question matchingQuestion = newQuestion().withFormInputs(asList(
                newFormInput().withType(FormInputType.TEXTAREA).build())
        ).build();

        Question notMatchingQuestion = newQuestion().withFormInputs(asList(
                newFormInput().withActive(false).withType(FormInputType.TEXTAREA).build())
        ).build();

        when(questionRepositoryMock.findByCompetitionId(competitionId)).thenReturn(asList(matchingQuestion, notMatchingQuestion));

        ServiceResult<Question> question = questionService.getQuestionByCompetitionIdAndFormInputType(competitionId, FormInputType.TEXTAREA);

        assertThat(question.isSuccess(), is(equalTo(true)));
        assertThat(question.getSuccessObject(), is(equalTo(matchingQuestion)));
    }

    @Test
    public void testGetQuestionByCompetitionIdAndFormInputTypeFailure() {
        long competitionId = 1L;

        Question notMatchingQuestion = newQuestion().withFormInputs(asList(
                newFormInput().withActive(false).withType(FormInputType.TEXTAREA).build())
        ).build();

        when(questionRepositoryMock.findByCompetitionId(competitionId)).thenReturn(asList(notMatchingQuestion));

        ServiceResult<Question> question = questionService.getQuestionByCompetitionIdAndFormInputType(competitionId, FormInputType.TEXTAREA);

        assertThat(question.isFailure(), is(equalTo(true)));
    }
}
