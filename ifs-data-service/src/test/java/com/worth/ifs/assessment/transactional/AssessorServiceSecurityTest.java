package com.worth.ifs.assessment.transactional;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.assessment.dto.Feedback;
import com.worth.ifs.assessment.security.FeedbackRules;
import com.worth.ifs.transactional.ServiceFailure;
import com.worth.ifs.transactional.ServiceSuccess;
import com.worth.ifs.util.Either;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import static com.worth.ifs.util.Either.right;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for testing security annotations on methods in the AssessorService
 */
public class AssessorServiceSecurityTest extends BaseServiceSecurityTest<AssessorService> {

    private FeedbackRules feedbackRules;

    @Before
    public void setup() {
        super.setup();
        feedbackRules = getMockPermissionRulesBean(FeedbackRules.class);
    }

    @Test
    public void test_updateAssessorFeedback_allowedBecauseUserIsAssessorOnAssessment() {

        Feedback feedback = new Feedback();
        when(feedbackRules.assessorCanUpdateTheirOwnFeedback(feedback, getLoggedInUser())).thenReturn(true);

        // call the method under test
        assertEquals("Security tested!", service.updateAssessorFeedback(feedback).getRight().getMessage());

        verify(feedbackRules).assessorCanUpdateTheirOwnFeedback(feedback, getLoggedInUser());
    }

    @Test
    public void test_updateAssessorFeedback_deniedBecauseUserIsNotAssessorOnAssessment() {

        Feedback feedback = new Feedback();
        when(feedbackRules.assessorCanUpdateTheirOwnFeedback(feedback, getLoggedInUser())).thenReturn(false);

        try {
            service.updateAssessorFeedback(feedback);
            fail("Should have thrown an AccessDeniedException");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }

        verify(feedbackRules).assessorCanUpdateTheirOwnFeedback(feedback, getLoggedInUser());
    }

    /**
     * Dummy AssessmentService implementation (for satisfying Spring Security's need to read parameter information from
     * methods, which is lost when using mocks)
     */
    private static class TestAssessmentService implements AssessorService {

        @Override
        public Either<ServiceFailure, ServiceSuccess> updateAssessorFeedback(Feedback feedback) {
            return right(new ServiceSuccess("Security tested!"));
        }
    }

    @Override
    protected Class<? extends AssessorService> getServiceClass() {
        return TestAssessmentService.class;
    }
}
