package com.worth.ifs.assessment.service;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.assessment.resource.AssessmentFeedbackResource;
import com.worth.ifs.commons.service.ServiceResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static com.worth.ifs.assessment.builder.AssessmentFeedbackResourceBuilder.newAssessmentFeedbackResource;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class AssessmentFeedbackServiceImplTest extends BaseServiceUnitTest<AssessmentFeedbackService> {
    @Mock
    private AssessmentFeedbackRestService assessmentFeedbackRestService;

    @Override
    protected AssessmentFeedbackService supplyServiceUnderTest() {
        return new AssessmentFeedbackServiceImpl();
    }

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void getAllAssessmentFeedback() throws Exception {
        final List<AssessmentFeedbackResource> expected = newAssessmentFeedbackResource()
                .withId(1L, 2L)
                .build(2);

        final Long assessmentId = 1L;

        when(assessmentFeedbackRestService.getAllAssessmentFeedback(assessmentId)).thenReturn(restSuccess(expected));

        final List<AssessmentFeedbackResource> found = service.getAllAssessmentFeedback(assessmentId);
        assertSame(expected, found);
        verify(assessmentFeedbackRestService, only()).getAllAssessmentFeedback(assessmentId);
    }

    @Test
    public void getAssessmentFeedbackByAssessmentAndQuestion() throws Exception {
        final AssessmentFeedbackResource expected = newAssessmentFeedbackResource()
                .build();

        final Long assessmentId = 1L;
        final Long questionId = 2L;

        when(assessmentFeedbackRestService.getAssessmentFeedbackByAssessmentAndQuestion(assessmentId, questionId)).thenReturn(restSuccess(expected));

        final AssessmentFeedbackResource response = service.getAssessmentFeedbackByAssessmentAndQuestion(assessmentId, questionId);

        assertSame(expected, response);
        verify(assessmentFeedbackRestService, only()).getAssessmentFeedbackByAssessmentAndQuestion(assessmentId, questionId);
    }

    @Test
    public void updateFeedbackValue() throws Exception {
        final Long assessmentId = 1L;
        final Long questionId = 2L;
        final String value = "Blah";

        when(assessmentFeedbackRestService.updateFeedbackValue(assessmentId, questionId, value)).thenReturn(restSuccess());

        final ServiceResult<Void> result = service.updateFeedbackValue(assessmentId, questionId, value);
        assertTrue(result.isSuccess());

        verify(assessmentFeedbackRestService, only()).updateFeedbackValue(assessmentId, questionId, value);
    }

    @Test
    public void updateFeedbackScore() throws Exception {
        final Long assessmentId = 1L;
        final Long questionId = 2L;
        final Integer score = 10;

        when(assessmentFeedbackRestService.updateFeedbackScore(assessmentId, questionId, score)).thenReturn(restSuccess());

        final ServiceResult<Void> result = service.updateFeedbackScore(assessmentId, questionId, score);
        assertTrue(result.isSuccess());

        verify(assessmentFeedbackRestService, only()).updateFeedbackScore(assessmentId, questionId, score);
    }
}