package com.worth.ifs.application.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.application.builder.QuestionAssessmentResourceBuilder;
import com.worth.ifs.application.resource.QuestionAssessmentResource;
import org.junit.Test;

import static org.junit.Assert.*;

public class QuestionAssessmentRestServiceMocksTest extends BaseRestServiceUnitTest<QuestionAssessmentRestServiceImpl> {

    private String questionAssessmentRestURL = "/questionAssessment";

    @Override
    protected QuestionAssessmentRestServiceImpl registerRestServiceUnderTest() {
        return new QuestionAssessmentRestServiceImpl();
    }

    @Test
    public void testFindById() {
        long id = 1L;
        QuestionAssessmentResource resource = QuestionAssessmentResourceBuilder.newQuestionAssessment().build();
        setupGetWithRestResultExpectations(questionAssessmentRestURL + "/" + id, QuestionAssessmentResource.class, resource);

        // now run the method under test
        assertTrue(service.findById(id).isSuccess());
    }

    @Test
    public void testFindByQuestionId() {
        long id = 1L;
        QuestionAssessmentResource resource = QuestionAssessmentResourceBuilder.newQuestionAssessment().build();
        setupGetWithRestResultExpectations(questionAssessmentRestURL + "/findByQuestion/" + id, QuestionAssessmentResource.class, resource);

        // now run the method under test
        assertTrue(service.findByQuestionId(id).isSuccess());
    }
}
