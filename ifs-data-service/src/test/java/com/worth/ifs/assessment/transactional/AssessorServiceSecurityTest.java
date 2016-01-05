package com.worth.ifs.assessment.transactional;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.assessment.dto.Feedback;
import com.worth.ifs.assessment.dto.Feedback.Id;
import com.worth.ifs.assessment.security.FeedbackLookup;
import com.worth.ifs.assessment.security.FeedbackRules;
import com.worth.ifs.transactional.ServiceFailure;
import com.worth.ifs.util.Either;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static com.worth.ifs.util.Either.right;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for testing security annotations on methods in the AssessorService interface
 */
public class AssessorServiceSecurityTest extends BaseServiceSecurityTest<AssessorService> {

    private FeedbackRules feedbackRules;
    private FeedbackLookup feedbackLookup;

    @Before
    public void setup() {
        super.setup();
        feedbackRules = getMockPermissionRulesBean(FeedbackRules.class);
        feedbackLookup = getMockPermissionEntityLookupStrategiesBean(FeedbackLookup.class);
    }

    @Test
    public void test_readAssessorFeedback_allowedBecauseUserIsAssessorOnAssessment() {
        Id id = new Id();
        Feedback feedback = new Feedback().setId(id);
        when(feedbackLookup.getFeedback(id)).thenReturn(feedback);
        when(feedbackRules.assessorCanReadTheirOwnFeedback(feedback, getLoggedInUser())).thenReturn(true);

        // call the method under test
        assertEquals("Security tested!", service.getFeedback(id).getRight().getValue().get());

        verify(feedbackRules).assessorCanReadTheirOwnFeedback(feedback, getLoggedInUser());
        verify(feedbackLookup).getFeedback(id);
    }


    @Test
    public void test_readAssessorFeedback_deniedBecauseUserIsNotAssessorOnAssessment() {
        Id id = new Id();
        Feedback feedback = new Feedback().setId(id);
        when(feedbackLookup.getFeedback(id)).thenReturn(feedback);
        when(feedbackRules.assessorCanReadTheirOwnFeedback(feedback, getLoggedInUser())).thenReturn(false);

        try {
            service.getFeedback(id);
            fail("Should have thrown an AccessDeniedException");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }

        verify(feedbackRules).assessorCanReadTheirOwnFeedback(feedback, getLoggedInUser());
    }


    @Test
    public void test_updateAssessorFeedback_allowedBecauseUserIsAssessorOnAssessment() {

        Feedback feedback = new Feedback();
        when(feedbackRules.assessorCanUpdateTheirOwnFeedback(feedback, getLoggedInUser())).thenReturn(true);

        // call the method under test
        assertEquals("Security tested!", service.updateAssessorFeedback(feedback).getRight().getValue().get());

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

    @Test
    public void test_updateAssessorFeedback_nullUserIsPassedToPermissionRuleMethods() {

        setLoggedInUser(null);
        Feedback feedback = new Feedback();
        when(feedbackRules.assessorCanUpdateTheirOwnFeedback(feedback, null)).thenReturn(false);

        try {
            service.updateAssessorFeedback(feedback);
            fail("Should have thrown an AccessDeniedException");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }

        verify(feedbackRules).assessorCanUpdateTheirOwnFeedback(feedback, null);
    }

    /**
     * Dummy AssessmentService implementation (for satisfying Spring Security's need to read parameter information from
     * methods, which is lost when using mocks)
     */
    private static class TestAssessmentService implements AssessorService {

        @Override
        public Either<ServiceFailure, Feedback> updateAssessorFeedback(Feedback feedback) {
            return right(new Feedback().setValue(Optional.of("Security tested!")));
        }

        @Override
        public Either<ServiceFailure, Feedback> getFeedback(Feedback.Id id) {
            return right(new Feedback().setValue(Optional.of("Security tested!")));
        }
    }

    @Override
    protected Class<? extends AssessorService> getServiceClass() {
        return TestAssessmentService.class;
    }
}
