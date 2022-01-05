package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotNull;
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
    public void getAssessmentPeriodByCompetitionId() {
        AssessmentPeriodResource assessmentPeriod = new AssessmentPeriodResource();
        assessmentPeriod.setId(1L);
        assessmentPeriod.setCompetitionId(competitionId);

        String url = assessmentPeriodRestURL + "?competitionId=" + competitionId;

        setupGetWithRestResultExpectations(url, ParameterizedTypeReferences.assessmentPeriodResourceListType(), Collections.singletonList(assessmentPeriod), OK);

        List<AssessmentPeriodResource> response = service.getAssessmentPeriodByCompetitionId(competitionId).getSuccess();
        assertNotNull(response);
        Assert.assertEquals(1, response.size());
        Assert.assertEquals(assessmentPeriod, response.get(0));
    }
}
