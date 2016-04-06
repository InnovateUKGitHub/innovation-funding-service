package com.worth.ifs.assessment.transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.dto.Feedback;
import com.worth.ifs.assessment.dto.Feedback.Id;
import com.worth.ifs.assessment.dto.Score;
import com.worth.ifs.assessment.security.FeedbackLookupStrategy;
import com.worth.ifs.assessment.security.FeedbackRules;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.workflow.domain.ProcessOutcome;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.method.P;

import static java.util.Optional.empty;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for testing security annotations on methods in the AssessorService interface
 */
public class AssessorServiceSecurityTest extends BaseServiceSecurityTest<AssessorService> {

    private FeedbackRules feedbackRules;
    private FeedbackLookupStrategy feedbackLookup;

    @Before
    public void setup() {
        super.setup();
        feedbackRules = getMockPermissionRulesBean(FeedbackRules.class);
        feedbackLookup = getMockPermissionEntityLookupStrategiesBean(FeedbackLookupStrategy.class);
    }

    @Test
    public void test_readAssessorFeedback_allowedBecauseUserIsAssessorOnAssessment() {
        Id id = new Id();
        Feedback feedback = new Feedback().setId(id);
        when(feedbackLookup.getFeedback(id)).thenReturn(feedback);
        when(feedbackRules.assessorCanReadTheirOwnFeedback(feedback, getLoggedInUser())).thenReturn(true);

        // call the method under test
        service.getFeedback(id);

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
    public void test_readAssessorFeedback_deniedBecauseFeedbackCouldNotBeFound() {

        Id id = new Id();
        when(feedbackLookup.getFeedback(id)).thenReturn(null);

        try {
            service.getFeedback(id);
            fail("Should have thrown an AccessDeniedException");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }

        verify(feedbackLookup).getFeedback(id);
    }


    @Test
    public void test_updateAssessorFeedback_allowedBecauseUserIsAssessorOnAssessment() {

        Feedback feedback = new Feedback();
        Id feedbackId = new Id(123L, 456L);

        when(feedbackLookup.getFeedback(feedbackId)).thenReturn(feedback);
        when(feedbackRules.assessorCanUpdateTheirOwnFeedback(feedback, getLoggedInUser())).thenReturn(true);

        service.updateAssessorFeedback(feedbackId, empty(), empty());

        verify(feedbackRules).assessorCanUpdateTheirOwnFeedback(feedback, getLoggedInUser());
    }

    @Test
    public void test_updateAssessorFeedback_deniedBecauseUserIsNotAssessorOnAssessment() {

        Feedback feedback = new Feedback();
        Id feedbackId = new Id(123L, 456L);

        when(feedbackLookup.getFeedback(feedbackId)).thenReturn(feedback);
        when(feedbackRules.assessorCanUpdateTheirOwnFeedback(feedback, getLoggedInUser())).thenReturn(false);

        try {
            service.updateAssessorFeedback(feedbackId, empty(), empty());
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
        Id feedbackId = new Id(123L, 456L);

        when(feedbackLookup.getFeedback(feedbackId)).thenReturn(feedback);
        when(feedbackRules.assessorCanUpdateTheirOwnFeedback(feedback, null)).thenReturn(false);

        try {
            service.updateAssessorFeedback(feedbackId, empty(), empty());
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
        public ServiceResult<Feedback> updateAssessorFeedback(@P("id") Id feedbackId, Optional<String> feedbackValue, Optional<String> feedbackText) {
            return null;
        }

        @Override
        public ServiceResult<Feedback> getFeedback(@P("id") Id id) {
            return null;
        }

        @Override
        public ServiceResult<Void> save(Assessment a) {
            return null;
        }

        @Override
        public ServiceResult<Assessment> saveAndGet(Assessment a) {
            return null;
        }

        @Override
        public ServiceResult<Assessment> getOne(Long id) {
            return null;
        }

        @Override
        public ServiceResult<List<Assessment>> getAllByCompetitionAndAssessor(Long competitionId, Long assessorId) {
            return null;
        }

        @Override
        public ServiceResult<Assessment> getOneByProcessRole(Long processRoleId) {
            return null;
        }

        @Override
        public ServiceResult<Integer> getTotalSubmittedAssessmentsByCompetition(Long competitionId, Long assessorId) {
            return null;
        }

        @Override
        public ServiceResult<Integer> getTotalAssignedAssessmentsByCompetition(Long competitionId, Long assessorId) {
            return null;
        }

        @Override
        public ServiceResult<Score> getScore(Long id) {
            return null;
        }

        @Override
        public ServiceResult<Void> submitAssessment(Long assessorId, Long applicationId, String suitableValue, String suitableFeedback, String comments) {
            return null;
        }

        @Override
        public ServiceResult<Void> acceptAssessmentInvitation(Long processRoleId, Assessment assessment) {
            return null;
        }

        @Override
        public ServiceResult<Void> rejectAssessmentInvitation(Long processRoleId, ProcessOutcome processOutcome) {
            return null;
        }

        @Override
        public ServiceResult<Void> submitAssessments(Set<Long> assessments) {
            return null;
        }
    }

    @Override
    protected Class<? extends AssessorService> getServiceClass() {
        return TestAssessmentService.class;
    }
}
