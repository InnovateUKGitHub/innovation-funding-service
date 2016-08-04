package com.worth.ifs.assessment.service;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
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
    public void test_getById() throws Exception {
        AssessmentResource expected = newAssessmentResource()
                .build();

        Long assessmentId = 1L;

        when(assessmentRestService.getById(assessmentId)).thenReturn(restSuccess(expected));

        AssessmentResource response = service.getById(assessmentId);

        assertSame(expected, response);
        verify(assessmentRestService, only()).getById(assessmentId);
    }

    @Test
    public void test_rejectApplication() throws Exception {
        Long assessmentId = 1L;
        String reason = "reason for rejection";
        String comment = "comment for rejection";

        ProcessOutcomeResource processOutcome = new ProcessOutcomeResource();
        processOutcome.setOutcomeType(AssessmentOutcomes.REJECT.getType());
        processOutcome.setComment(comment);
        processOutcome.setDescription(reason);

        when(assessmentRestService.updateStatus(assessmentId, processOutcome)).thenReturn(restSuccess());
        ServiceResult<Void> response = service.rejectApplication(assessmentId, reason, comment);

        assertTrue(response.isSuccess());
        verify(assessmentRestService, only()).updateStatus(assessmentId, processOutcome);
    }
}