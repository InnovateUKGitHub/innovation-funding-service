package com.worth.ifs.assessment.service;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.assessment.resource.AssessmentResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertSame;
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
        final AssessmentResource expected = newAssessmentResource()
                .build();

        final Long assessmentId = 9999L;

        when(assessmentRestService.getById(assessmentId)).thenReturn(restSuccess(expected));

        final AssessmentResource response = service.getById(assessmentId);

        assertSame(expected, response);
        verify(assessmentRestService, only()).getById(9999L);
    }
}