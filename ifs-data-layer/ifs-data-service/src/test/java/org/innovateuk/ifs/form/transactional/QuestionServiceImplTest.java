package org.innovateuk.ifs.form.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.mapper.QuestionMapper;
import org.innovateuk.ifs.form.mapper.SectionMapper;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.repository.QuestionRepository;
import org.innovateuk.ifs.form.repository.SectionRepository;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.form.builder.SectionBuilder.newSection;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.APPLICATION_DETAILS;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

public class QuestionServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    protected QuestionService questionService = new QuestionServiceImpl();

    @Mock
    private QuestionRepository questionRepositoryMock;

    @Mock
    private QuestionMapper questionMapperMock;

    @Mock
    private SectionService sectionService;

    @Mock
    private SectionRepository sectionRepositoryMock;

    @Mock
    private AssessmentRepository assessmentRepositoryMock;

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Mock
    private SectionMapper sectionMapperMock;

    @Mock
    private FormInputRepository formInputRepositoryMock;

    @Test
    public void getNextQuestionTest() throws Exception {
        Question question = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 1).build();
        Question nextQuestion = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 2).build();
        QuestionResource nextQuestionResource = newQuestionResource().withCompetitionAndSectionAndPriority(newCompetitionResource().build(), newSectionResource().build(), 2).build();

        when(questionRepositoryMock.findById(question.getId())).thenReturn(Optional.of(question));
        when(questionRepositoryMock.findFirstByCompetitionIdAndSectionIdAndPriorityGreaterThanOrderByPriorityAsc(
                question.getCompetition().getId(), question.getSection().getId(), question.getPriority()))
                .thenReturn(nextQuestion);
        when(questionMapperMock.mapToResource(nextQuestion)).thenReturn(nextQuestionResource);

        // Method under test
        assertEquals(nextQuestionResource, questionService.getNextQuestion(question.getId()).getSuccess());
    }

    @Test
    public void getPreviousQuestionTest() throws Exception {
        Question question = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 2).build();
        Question previousQuestion = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 1).build();
        QuestionResource previousQuestionResource = newQuestionResource().withCompetitionAndSectionAndPriority(newCompetitionResource().build(), newSectionResource().build(), 1).build();

        when(questionRepositoryMock.findById(question.getId())).thenReturn(Optional.of(question));
        when(questionRepositoryMock.findFirstByCompetitionIdAndSectionIdAndPriorityLessThanOrderByPriorityDesc(
                question.getCompetition().getId(), question.getSection().getId(), question.getPriority()))
                .thenReturn(previousQuestion);
        when(questionMapperMock.mapToResource(previousQuestion)).thenReturn(previousQuestionResource);

        // Method under test
        assertEquals(previousQuestionResource, questionService.getPreviousQuestion(question.getId()).getSuccess());
    }

    @Test
    public void getNextQuestionFromOtherSectionTest() throws Exception {
        Section nextSection = newSection().build();
        SectionResource nextSectionResource = newSectionResource().build();
        Question question = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), newSection().build(), 1).build();
        Question nextQuestion = newQuestion().withCompetitionAndSectionAndPriority(newCompetition().build(), nextSection, 2).build();
        QuestionResource nextQuestionResource = newQuestionResource().withCompetitionAndSectionAndPriority(newCompetitionResource().build(), nextSectionResource, 2).build();

        when(questionRepositoryMock.findById(question.getId())).thenReturn(Optional.of(question));
        when(sectionService.getNextSection(any(SectionResource.class))).thenReturn(serviceSuccess(nextSectionResource));
        when(questionRepositoryMock.findFirstByCompetitionIdAndSectionIdAndPriorityGreaterThanOrderByPriorityAsc(
                question.getCompetition().getId(), question.getSection().getId(), question.getPriority())).thenReturn(nextQuestion);
        when(questionMapperMock.mapToResource(nextQuestion)).thenReturn(nextQuestionResource);

        // Method under test
        assertEquals(nextQuestionResource, questionService.getNextQuestion(question.getId()).getSuccess());
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

        when(questionRepositoryMock.findById(question.getId())).thenReturn(Optional.of(question));
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
        assertEquals(previousQuestionResource, questionService.getPreviousQuestion(question.getId()).getSuccess());

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
        when(questionRepositoryMock.findById(anyLong())).thenReturn(Optional.of(previousSectionQuestion));
        // Method under test
        when(questionMapperMock.mapToResource(previousSectionQuestion)).thenReturn(previousSectionQuestionResource);

        assertEquals(previousSectionQuestionResource, questionService.getPreviousQuestionBySection(currentSection.getId()).getSuccess());
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

        when(sectionRepositoryMock.findById(1L)).thenReturn(Optional.of(parentSection));

        QuestionResource questionResource1 = newQuestionResource().build();
        QuestionResource questionResource2 = newQuestionResource().build();
        QuestionResource questionResource3 = newQuestionResource().build();

        when(questionMapperMock.mapToResource(child1CostQuestion)).thenReturn(questionResource1);
        when(questionMapperMock.mapToResource(child2CostQuestion)).thenReturn(questionResource2);
        when(questionMapperMock.mapToResource(parentCostQuestion)).thenReturn(questionResource3);


        ServiceResult<List<QuestionResource>> result = questionService.getQuestionsBySectionIdAndType(1L, QuestionType.COST);

        assertTrue(result.isSuccess());
        assertEquals(3, result.getSuccess().size());
        assertTrue(result.getSuccess().contains(questionResource1));
        assertTrue(result.getSuccess().contains(questionResource2));
        assertTrue(result.getSuccess().contains(questionResource3));
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
        assertEquals(questionResource, result.getSuccess());
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

        when(questionRepositoryMock.findById(questionId)).thenReturn(Optional.of(question));
        when(assessmentRepositoryMock.findById(assessmentId)).thenReturn(Optional.of(assessment));
        when(questionMapperMock.mapToResource(question)).thenReturn(questionResource);

        ServiceResult<QuestionResource> result = questionService.getQuestionByIdAndAssessmentId(questionId, assessmentId);

        assertTrue(result.isSuccess());
        assertEquals(questionResource, result.getSuccess());

        InOrder inOrder = inOrder(assessmentRepositoryMock, questionRepositoryMock, questionMapperMock);
        inOrder.verify(assessmentRepositoryMock).findById(assessmentId);
        inOrder.verify(questionRepositoryMock).findById(questionId);
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
        inOrder.verify(assessmentRepositoryMock).findById(assessmentId);
        inOrder.verifyNoMoreInteractions();

        verifyNoInteractions(questionRepositoryMock);
        verifyNoInteractions(questionMapperMock);
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

        when(assessmentRepositoryMock.findById(assessmentId)).thenReturn(Optional.of(assessment));

        ServiceResult<QuestionResource> result = questionService.getQuestionByIdAndAssessmentId(questionId, assessmentId);
        assertTrue(result.getFailure().is(notFoundError(Question.class, questionId)));

        InOrder inOrder = inOrder(assessmentRepositoryMock, questionRepositoryMock);
        inOrder.verify(assessmentRepositoryMock).findById(assessmentId);
        inOrder.verify(questionRepositoryMock).findById(questionId);
        inOrder.verifyNoMoreInteractions();

        verifyNoInteractions(questionMapperMock);
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

        when(questionRepositoryMock.findById(questionId)).thenReturn(Optional.of(question));
        when(assessmentRepositoryMock.findById(assessmentId)).thenReturn(Optional.of(assessment));

        ServiceResult<QuestionResource> result = questionService.getQuestionByIdAndAssessmentId(questionId, assessmentId);
        assertTrue(result.getFailure().is(notFoundError(Question.class, questionId, assessmentId)));

        InOrder inOrder = inOrder(assessmentRepositoryMock, questionRepositoryMock);
        inOrder.verify(assessmentRepositoryMock).findById(assessmentId);
        inOrder.verify(questionRepositoryMock).findById(questionId);
        inOrder.verifyNoMoreInteractions();

        verifyNoInteractions(questionMapperMock);
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

        when(applicationRepositoryMock.findById(application.getId())).thenReturn(Optional.of(application));
        when(assessmentRepositoryMock.findById(assessmentId)).thenReturn(Optional.of(assessment));
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
        assertEquals(expectedQuestions, result.getSuccess());
    }

    @Test
    public void testGetQuestionByCompetitionIdAndFormInputTypeSuccess() {
        long competitionId = 1L;

        Question question = newQuestion().build();

        FormInput formInput = newFormInput().
                withType(FormInputType.TEXTAREA).
                withQuestion(question).
                build();

        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competitionId, singletonList(FormInputType.TEXTAREA))).
                thenReturn(singletonList(formInput));

        ServiceResult<Question> result =
                questionService.getQuestionByCompetitionIdAndFormInputType(competitionId, FormInputType.TEXTAREA);

        assertThat(result.isSuccess(), equalTo(true));
        assertThat(result.getSuccess(), equalTo(question));
    }

    @Test
    public void getQuestionByCompetitionIdAndQuestionSetupType() {
        long competitionId = 1L;

        Question question = newQuestion().build();

        QuestionResource questionResource = newQuestionResource()
                .build();

        when(questionRepositoryMock.findFirstByCompetitionIdAndQuestionSetupType(competitionId,
                APPLICATION_DETAILS)).thenReturn(question);
        when(questionMapperMock.mapToResource(same(question))).thenReturn(questionResource);

        ServiceResult<QuestionResource> result = questionService
                .getQuestionByCompetitionIdAndQuestionSetupType(competitionId,
                        APPLICATION_DETAILS);

        assertTrue(result.isSuccess());
        assertEquals(questionResource, result.getSuccess());

        verify(questionRepositoryMock).findFirstByCompetitionIdAndQuestionSetupType(competitionId, APPLICATION_DETAILS);
        verify(questionMapperMock, only()).mapToResource(question);
    }
}
