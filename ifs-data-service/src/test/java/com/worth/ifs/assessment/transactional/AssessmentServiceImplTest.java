package com.worth.ifs.assessment.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.AssessmentResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class AssessmentServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private final AssessmentService assessmentService = new AssessmentServiceImpl();

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void findById() throws Exception {
        final Assessment assessment = newAssessment().build();

        final AssessmentResource expected = newAssessmentResource().build();

        when(assessmentRepositoryMock.findOne(9999L)).thenReturn(assessment);
        when(assessmentMapperMock.mapToResource(same(assessment))).thenReturn(expected);

        final AssessmentResource found = assessmentService.findById(9999L).getSuccessObject();

        assertSame(expected, found);
        verify(assessmentRepositoryMock, only()).findOne(9999L);
    }

}