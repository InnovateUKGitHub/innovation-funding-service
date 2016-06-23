package com.worth.ifs.assessment.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.assessment.resource.AssessmentResource;
import org.junit.Before;
import org.junit.Test;

import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static java.lang.String.format;
import static org.junit.Assert.assertSame;
import static org.springframework.http.HttpStatus.OK;

public class AssessmentRestServiceImplTest extends BaseRestServiceUnitTest<AssessmentRestServiceImpl> {

    private static final String assessmentRestURL = "/assessment";

    @Before
    public void setUp() throws Exception {

    }

    @Override
    protected AssessmentRestServiceImpl registerRestServiceUnderTest() {
        final AssessmentRestServiceImpl assessmentRestService = new AssessmentRestServiceImpl();
        assessmentRestService.setAssessmentRestURL(assessmentRestURL);
        return assessmentRestService;
    }

    @Test
    public void getById() throws Exception {
        final AssessmentResource expected = newAssessmentResource()
                .build();

        final Long assessmentId = 9999L;

        setupGetWithRestResultExpectations(format("%s/%s", assessmentRestURL, assessmentId), AssessmentResource.class, expected, OK);
        final AssessmentResource response = service.getById(assessmentId).getSuccessObject();
        assertSame(expected, response);
    }
}