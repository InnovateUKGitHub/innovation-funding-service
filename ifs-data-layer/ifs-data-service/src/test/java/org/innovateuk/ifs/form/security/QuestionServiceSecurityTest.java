package org.innovateuk.ifs.form.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.security.AssessmentLookupStrategy;
import org.innovateuk.ifs.assessment.security.AssessmentPermissionRules;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.form.transactional.QuestionService;
import org.innovateuk.ifs.form.transactional.QuestionServiceImpl;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.form.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

/**
 * Testing how the secured methods in QuestionSetupCompetitionService interact with Spring Security
 */
public class QuestionServiceSecurityTest extends BaseServiceSecurityTest<QuestionService> {

    private static final int ARRAY_SIZE_FOR_POST_FILTER_TESTS = 2;

    private QuestionPermissionRules questionPermissionRules;
    private QuestionResourceLookupStrategy questionResourceLookupStrategy;

    private AssessmentPermissionRules assessmentPermissionRules;
    private AssessmentLookupStrategy assessmentLookupStrategy;

    @Before
    public void lookupPermissionRules() {
        questionPermissionRules = getMockPermissionRulesBean(QuestionPermissionRules.class);
        questionResourceLookupStrategy = getMockPermissionEntityLookupStrategiesBean(QuestionResourceLookupStrategy
                .class);

        assessmentPermissionRules = getMockPermissionRulesBean(AssessmentPermissionRules.class);
        assessmentLookupStrategy = getMockPermissionEntityLookupStrategiesBean(AssessmentLookupStrategy.class);
    }

    @Test
    public void testFindByCompetition() {
        final Long competitionId = 1L;

        when(classUnderTestMock.findByCompetition(competitionId))
                .thenReturn(serviceSuccess(newQuestionResource().build(ARRAY_SIZE_FOR_POST_FILTER_TESTS)));

        classUnderTest.findByCompetition(competitionId);
        verify(questionPermissionRules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS))
                .loggedInUsersCanSeeAllQuestions(isA(QuestionResource.class), isA(UserResource.class));
    }

    @Test
    public void testGetQuestionById() {
        final Long questionId = 1L;
        when(questionResourceLookupStrategy.findResourceById(questionId)).thenReturn(newQuestionResource().build());
        assertAccessDenied(
                () -> classUnderTest.getQuestionById(questionId),
                () -> verify(questionPermissionRules)
                        .loggedInUsersCanSeeAllQuestions(isA(QuestionResource.class), isA(UserResource.class))
        );
    }

    @Test
    public void testGetNextQuestion() {
        final Long questionId = 1L;

        when(classUnderTestMock.getNextQuestion(questionId)).thenReturn(serviceSuccess(newQuestionResource().build()));

        assertAccessDenied(
                () -> classUnderTest.getNextQuestion(questionId),
                () -> verify(questionPermissionRules)
                        .loggedInUsersCanSeeAllQuestions(isA(QuestionResource.class), isA(UserResource.class))
        );
    }

    @Test
    public void testGetPreviousQuestionBySection() {
        final Long sectionId = 1L;

        when(classUnderTestMock.getPreviousQuestionBySection(sectionId)).thenReturn(serviceSuccess
                (newQuestionResource().build()));

        assertAccessDenied(
                () -> classUnderTest.getPreviousQuestionBySection(sectionId),
                () -> verify(questionPermissionRules)
                        .loggedInUsersCanSeeAllQuestions(isA(QuestionResource.class), isA(UserResource.class))
        );
    }

    @Test
    public void testGetNextQuestionBySection() {
        final Long sectionId = 1L;

        when(classUnderTestMock.getNextQuestionBySection(sectionId)).thenReturn(serviceSuccess(newQuestionResource()
                .build()));

        assertAccessDenied(
                () -> classUnderTest.getNextQuestionBySection(sectionId),
                () -> verify(questionPermissionRules)
                        .loggedInUsersCanSeeAllQuestions(isA(QuestionResource.class), isA(UserResource.class))
        );
    }

    @Test
    public void testGetPreviousQuestion() {
        final Long sectionId = 1L;

        when(classUnderTestMock.getPreviousQuestion(sectionId)).thenReturn(serviceSuccess(newQuestionResource().build
                ()));

        assertAccessDenied(
                () -> classUnderTest.getPreviousQuestion(sectionId),
                () -> verify(questionPermissionRules)
                        .loggedInUsersCanSeeAllQuestions(isA(QuestionResource.class), isA(UserResource.class))
        );
    }

    @Test
    public void testQuestionResourceByCompetitionIdAndFormInputType() {
        when(classUnderTestMock.getQuestionResourceByCompetitionIdAndFormInputType(null, null))
                .thenReturn(serviceSuccess(newQuestionResource().build()));

        assertAccessDenied(
                () -> classUnderTest.getQuestionResourceByCompetitionIdAndFormInputType(null, null),
                () -> verify(questionPermissionRules)
                        .loggedInUsersCanSeeAllQuestions(isA(QuestionResource.class), isA(UserResource.class))
        );
    }

    @Test
    public void testGetQuestionByCompetitionIdAndFormInputType() {
        when(classUnderTestMock.getQuestionByCompetitionIdAndFormInputType(null, null))
                .thenReturn(serviceSuccess(newQuestion().build()));

        assertAccessDenied(
                () -> classUnderTest.getQuestionByCompetitionIdAndFormInputType(null, null),
                () -> verify(questionPermissionRules)
                        .loggedInUsersCanSeeAllQuestions(isA(Question.class), isA(UserResource.class))
        );
    }

    @Test
    public void testGetQuestionsBySectionIdAndType() {
        when(classUnderTestMock.getQuestionsBySectionIdAndType(1L, QuestionType.GENERAL))
                .thenReturn(serviceSuccess(newQuestionResource().build(ARRAY_SIZE_FOR_POST_FILTER_TESTS)));

        classUnderTest.getQuestionsBySectionIdAndType(1L, QuestionType.GENERAL);

        verify(questionPermissionRules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS))
                .loggedInUsersCanSeeAllQuestions(isA(QuestionResource.class), isA(UserResource.class));
    }

    @Test
    public void testGetQuestionByIdAndAssessmentId() {
        Long questionId = 1L;
        Long assessmentId = 2L;

        when(assessmentLookupStrategy.getAssessmentResource(assessmentId)).thenReturn(newAssessmentResource().build());
        assertAccessDenied(
                () -> classUnderTest.getQuestionByIdAndAssessmentId(questionId, assessmentId),
                () -> verify(assessmentPermissionRules)
                        .userCanReadAssessment(isA(AssessmentResource.class), isA(UserResource.class))
        );
    }

    @Test
    public void testGetQuestionsByAssessmentId() {
        Long assessmentId = 1L;

        when(assessmentLookupStrategy.getAssessmentResource(assessmentId)).thenReturn(newAssessmentResource().build());

        assertAccessDenied(
                () -> classUnderTest.getQuestionsByAssessmentId(assessmentId),
                () -> verify(assessmentPermissionRules)
                        .userCanReadAssessment(isA(AssessmentResource.class), isA(UserResource.class))
        );
    }

    @Override
    protected Class<? extends QuestionService> getClassUnderTest() {
        return QuestionServiceImpl.class;
    }
}

