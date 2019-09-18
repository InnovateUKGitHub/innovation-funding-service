package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.security.ApplicationLookupStrategy;
import org.innovateuk.ifs.application.security.ApplicationPermissionRules;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponsesResource;
import org.innovateuk.ifs.assessment.transactional.AssessorFormInputResponseService;
import org.innovateuk.ifs.assessment.transactional.AssessorFormInputResponseServiceImpl;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AssessorFormInputResponseServiceSecurityTest extends BaseServiceSecurityTest<AssessorFormInputResponseService> {

    private AssessorFormInputResponsePermissionRules assessorFormInputResponsePermissionRules;
    private AssessmentLookupStrategy assessmentLookupStrategy;
    private AssessmentPermissionRules assessmentPermissionRules;
    private ApplicationLookupStrategy applicationLookupStrategy;
    private ApplicationPermissionRules applicationPermissionRules;

    @Override
    protected Class<? extends AssessorFormInputResponseService> getClassUnderTest() {
        return AssessorFormInputResponseServiceImpl.class;
    }

    @Before
    public void setUp() throws Exception {
        assessorFormInputResponsePermissionRules = getMockPermissionRulesBean(AssessorFormInputResponsePermissionRules.class);
        assessmentLookupStrategy = getMockPermissionEntityLookupStrategiesBean(AssessmentLookupStrategy.class);
        assessmentPermissionRules = getMockPermissionRulesBean(AssessmentPermissionRules.class);
        applicationLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ApplicationLookupStrategy.class);
        applicationPermissionRules = getMockPermissionRulesBean(ApplicationPermissionRules.class);
    }

    @Test
    public void getAllAssessorFormInputResponses() {
        Long assessmentId = 1L;

        when(assessmentLookupStrategy.getAssessmentResource(assessmentId)).thenReturn(newAssessmentResource().build());
        assertAccessDenied(
                () -> classUnderTest.getAllAssessorFormInputResponses(assessmentId),
                () -> verify(assessmentPermissionRules).userCanReadAssessment(isA(AssessmentResource.class), isA(UserResource.class))
        );
    }

    @Test
    public void getAllAssessorFormInputResponsesByAssessmentAndQuestion() {
        Long assessmentId = 1L;
        Long questionId = 3L;

        when(assessmentLookupStrategy.getAssessmentResource(assessmentId)).thenReturn(newAssessmentResource().build());
        assertAccessDenied(
                () -> classUnderTest.getAllAssessorFormInputResponsesByAssessmentAndQuestion(assessmentId, questionId),
                () -> verify(assessmentPermissionRules).userCanReadAssessment(isA(AssessmentResource.class), isA(UserResource.class))
        );
    }

    @Test
    public void updateFormInputResponses() {
        AssessorFormInputResponsesResource responses = new AssessorFormInputResponsesResource();

        assertAccessDenied(
                () -> classUnderTest.updateFormInputResponses(responses),
                () -> verify(assessorFormInputResponsePermissionRules).userCanUpdateAssessorFormInputResponses(
                        eq(responses), isA(UserResource.class))
        );
    }

    @Test
    public void getApplicationAggregateScores() {
        long applicationId = 2L;

        when(applicationLookupStrategy.getApplicationResource(applicationId)).thenReturn(newApplicationResource().build());
        assertAccessDenied(
                () -> classUnderTest.getApplicationAggregateScores(applicationId),
                () -> verify(applicationPermissionRules).usersConnectedToTheApplicationCanView(isA(ApplicationResource.class), isA(UserResource.class))
        );
    }

    @Test
    public void getAssessmentAggregateFeedback() {
        long applicationId = 1L;
        long questionId = 2L;
        when(applicationLookupStrategy.getApplicationResource(applicationId)).thenReturn(newApplicationResource().build());
        assertAccessDenied(
                () -> classUnderTest.getAssessmentAggregateFeedback(applicationId, questionId),
                () -> verify(applicationPermissionRules).usersConnectedToTheApplicationCanView(isA(ApplicationResource.class), isA(UserResource.class))
        );
    }
}
