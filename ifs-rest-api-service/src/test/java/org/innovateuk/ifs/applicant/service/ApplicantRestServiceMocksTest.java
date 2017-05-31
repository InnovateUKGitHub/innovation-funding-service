package org.innovateuk.ifs.applicant.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.junit.Test;

import static java.lang.String.format;
import static org.junit.Assert.*;

public class ApplicantRestServiceMocksTest extends BaseRestServiceUnitTest<ApplicantRestServiceImpl> {

    @Override
    protected ApplicantRestServiceImpl registerRestServiceUnderTest() {
        return new ApplicantRestServiceImpl();
    }

    @Test
    public void getQuestion() throws Exception {
        ApplicantQuestionResource expected = new ApplicantQuestionResource();

        long applicationId = 1L;
        long userId = 2L;
        long questionId = 3L;

        setupGetWithRestResultExpectations(format("/applicant/%s/%s/question/%s",
                userId, applicationId, questionId), ApplicantQuestionResource.class, expected);

        assertSame(expected, service.getQuestion(userId, applicationId, questionId));
    }

    @Test
    public void getSection() throws Exception {
        ApplicantSectionResource expected = new ApplicantSectionResource();

        long applicationId = 1L;
        long userId = 2L;
        long sectionId = 3L;

        setupGetWithRestResultExpectations(format("/applicant/%s/%s/section/%s",
                userId, applicationId, sectionId), ApplicantSectionResource.class, expected);

        assertSame(expected, service.getSection(userId, applicationId, sectionId));
    }

}