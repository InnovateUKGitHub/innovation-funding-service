package com.worth.ifs.assessment.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.assessment.domain.AssessmentFeedback;
import com.worth.ifs.assessment.resource.AssessmentFeedbackResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.List;

import static com.worth.ifs.assessment.builder.AssessmentFeedbackBuilder.newAssessmentFeedback;
import static com.worth.ifs.assessment.builder.AssessmentFeedbackResourceBuilder.newAssessmentFeedbackResource;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class AssessmentFeedbackServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private final AssessmentFeedbackService assessmentFeedbackService = new AssessmentFeedbackServiceImpl();

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void getAllAssessmentFeedback() throws Exception {
        final AssessmentFeedback assessmentFeedback1 = newAssessmentFeedback().build();
        final AssessmentFeedback assessmentFeedback2 = newAssessmentFeedback().build();

        final List<AssessmentFeedback> assessmentFeedback = asList(assessmentFeedback1, assessmentFeedback2);

        final AssessmentFeedbackResource expected1 = newAssessmentFeedbackResource()
                .build();

        final AssessmentFeedbackResource expected2 = newAssessmentFeedbackResource()
                .build();

        when(assessmentFeedbackRepositoryMock.findByAssessmentId(9999L)).thenReturn(assessmentFeedback);
        when(assessmentFeedbackMapperMock.mapToResource(same(assessmentFeedback1))).thenReturn(expected1);
        when(assessmentFeedbackMapperMock.mapToResource(same(assessmentFeedback2))).thenReturn(expected2);

        final List<AssessmentFeedbackResource> found = assessmentFeedbackService.getAllAssessmentFeedback(9999L).getSuccessObject();

        assertSame(expected1, found.get(0));
        assertSame(expected2, found.get(1));
        verify(assessmentFeedbackRepositoryMock, only()).findByAssessmentId(9999L);
    }

    @Test
    public void getAssessmentFeedbackByAssessmentAndQuestion() throws Exception {
        final AssessmentFeedback assessmentFeedback = newAssessmentFeedback().build();

        final AssessmentFeedbackResource expected = newAssessmentFeedbackResource()
                .build();

        when(assessmentFeedbackRepositoryMock.findByAssessmentIdAndQuestionId(9999L, 8888L)).thenReturn(assessmentFeedback);
        when(assessmentFeedbackMapperMock.mapToResource(same(assessmentFeedback))).thenReturn(expected);

        final AssessmentFeedbackResource found = assessmentFeedbackService.getAssessmentFeedbackByAssessmentAndQuestion(9999L, 8888L).getSuccessObject();

        assertSame(expected, found);
        verify(assessmentFeedbackRepositoryMock, only()).findByAssessmentIdAndQuestionId(9999L, 8888L);
    }
}