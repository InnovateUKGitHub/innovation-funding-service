package com.worth.ifs.assessment.service;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.assessment.resource.ApplicationRejectionResource;
import com.worth.ifs.assessment.resource.AssessmentFundingDecisionResource;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.resource.AssessmentSubmissionsResource;
import com.worth.ifs.commons.service.ServiceResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static com.worth.ifs.assessment.builder.ApplicationRejectionResourceBuilder.newApplicationRejectionResource;
import static com.worth.ifs.assessment.builder.AssessmentFundingDecisionResourceBuilder.newAssessmentFundingDecisionResource;
import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static com.worth.ifs.assessment.builder.AssessmentSubmissionsResourceBuilder.newAssessmentSubmissionsResource;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
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

        AssessmentResource response = service.getById(assessmentId);

        assertSame(expected, response);
        verify(assessmentRestService, only()).getById(assessmentId);
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