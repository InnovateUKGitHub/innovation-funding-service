package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

public class AssessmentPeriodRestServiceMocksTest extends BaseRestServiceUnitTest<AssessmentPeriodRestServiceImpl> {

    private static final String assessmentPeriodRestURL = "/assessment-period";
    private static final Long competitionId = 1L;
    private static final Integer index = 1;

    @Override
    protected AssessmentPeriodRestServiceImpl registerRestServiceUnderTest() {
        return new AssessmentPeriodRestServiceImpl();
    }

    @Test
    public void getAssessmentPeriodByCompetitionIdAndIndex() {
        AssessmentPeriodResource assessmentPeriod = new AssessmentPeriodResource();
        assessmentPeriod.setId(1L);
        assessmentPeriod.setCompetitionId(competitionId);
        assessmentPeriod.setIndex(index);

        String url = assessmentPeriodRestURL + "/" + competitionId + "/get-by-index?index=" + index;

        setupGetWithRestResultExpectations(url, AssessmentPeriodResource.class, assessmentPeriod, OK);

        AssessmentPeriodResource response = service.getAssessmentPeriodByCompetitionIdAndIndex(index, competitionId).getSuccess();
        assertNotNull(response);
        Assert.assertEquals(assessmentPeriod, response);
    }

    @Test
    public void createAssessmentPeriod() {
        AssessmentPeriodResource assessmentPeriod = new AssessmentPeriodResource();
        assessmentPeriod.setId(1L);
        assessmentPeriod.setCompetitionId(competitionId);
        assessmentPeriod.setIndex(index);

        String url = assessmentPeriodRestURL + "/" + competitionId + "?index=" + index;

        setupPostWithRestResultExpectations(url, AssessmentPeriodResource.class, null, assessmentPeriod, CREATED);

        AssessmentPeriodResource response = service.create(index, competitionId).getSuccess();
        assertNotNull(response);
        Assert.assertEquals(assessmentPeriod, response);
    }
}
