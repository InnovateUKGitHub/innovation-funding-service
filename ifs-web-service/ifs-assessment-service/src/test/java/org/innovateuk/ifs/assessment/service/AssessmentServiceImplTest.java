package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.innovateuk.ifs.assessment.builder.ApplicationRejectionResourceBuilder.newApplicationRejectionResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentFundingDecisionResourceBuilder.newAssessmentFundingDecisionResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentSubmissionsResourceBuilder.newAssessmentSubmissionsResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentTotalScoreResourceBuilder.newAssessmentTotalScoreResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class AssessmentServiceImplTest extends BaseServiceUnitTest<AssessmentService> {
    @Mock
    private AssessmentRestService assessmentRestService;

    @Override
    protected AssessmentService supplyServiceUnderTest() {
        return new AssessmentServiceImpl();
    }

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void getById() throws Exception {
        AssessmentResource expected = newAssessmentResource()
                .build();

        Long assessmentId = 1L;

        when(assessmentRestService.getById(assessmentId)).thenReturn(restSuccess(expected));

        assertSame(expected, service.getById(assessmentId));
        verify(assessmentRestService, only()).getById(assessmentId);
    }

    @Test
    public void getByUserAndCompetition() throws Exception {
        List<AssessmentResource> expected = newAssessmentResource().build(2);

        Long userId = 1L;
        Long competitionId = 2L;

        when(assessmentRestService.getByUserAndCompetition(userId, competitionId)).thenReturn(restSuccess(expected));

        assertSame(expected, service.getByUserAndCompetition(userId, competitionId));
        verify(assessmentRestService, only()).getByUserAndCompetition(userId, competitionId);
    }

    @Test
    public void getTotalScore() throws Exception {
        AssessmentTotalScoreResource expected = newAssessmentTotalScoreResource().build();

        Long assessmentId = 1L;

        when(assessmentRestService.getTotalScore(assessmentId)).thenReturn(restSuccess(expected));

        assertSame(expected, service.getTotalScore(assessmentId));
        verify(assessmentRestService, only()).getTotalScore(assessmentId);
    }

    @Test
    public void recommend() throws Exception {
        Long assessmentId = 1L;
        String feedback = "feedback for decision";
        String comment = "comment for decision";

        AssessmentFundingDecisionResource assessmentFundingDecision = newAssessmentFundingDecisionResource()
                .withFundingConfirmation(TRUE)
                .withFeedback(feedback)
                .withComment(comment)
                .build();

        when(assessmentRestService.recommend(assessmentId, assessmentFundingDecision)).thenReturn(restSuccess());

        ServiceResult<Void> response = service.recommend(assessmentId, TRUE, feedback, comment);

        assertTrue(response.isSuccess());
        verify(assessmentRestService, only()).recommend(assessmentId, assessmentFundingDecision);
    }

    @Test
    public void rejectInvitation() throws Exception {
        Long assessmentId = 1L;
        String reason = "reason for rejection";
        String comment = "comment for rejection";

        ApplicationRejectionResource applicationRejection = newApplicationRejectionResource()
                .withRejectReason(reason)
                .withRejectComment(comment)
                .build();

        when(assessmentRestService.rejectInvitation(assessmentId, applicationRejection)).thenReturn(restSuccess());

        ServiceResult<Void> response = service.rejectInvitation(assessmentId, reason, comment);

        assertTrue(response.isSuccess());
        verify(assessmentRestService, only()).rejectInvitation(assessmentId, applicationRejection);
    }

    @Test
    public void acceptInvitation() throws Exception {
        Long assessmentId = 1L;

        when(assessmentRestService.acceptInvitation(assessmentId)).thenReturn(restSuccess());

        ServiceResult<Void> response = service.acceptInvitation(assessmentId);

        assertTrue(response.isSuccess());
        verify(assessmentRestService, only()).acceptInvitation(assessmentId);
    }

    @Test
    public void submitAssessments() throws Exception {
        AssessmentSubmissionsResource assessmentSubmissions = newAssessmentSubmissionsResource()
                .withAssessmentIds(asList(1L, 2L))
                .build();

        when(assessmentRestService.submitAssessments(assessmentSubmissions)).thenReturn(restSuccess());

        ServiceResult<Void> response = service.submitAssessments(asList(1L, 2L));

        assertTrue(response.isSuccess());
        verify(assessmentRestService, only()).submitAssessments(assessmentSubmissions);
    }
}
