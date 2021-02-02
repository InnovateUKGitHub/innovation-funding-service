package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.springframework.http.HttpStatus.OK;

public class AssessmentRestServiceMocksTest extends BaseRestServiceUnitTest<AssessmentPeriodRestServiceImpl> {

    private static final String assessmentPeriodUrl = "/assessment-period";
    private static final Long newCompetitionId = 2L;

    @Test
    public void addNewAssessmentPeriod() {
        String url = assessmentPeriodUrl + "/" + newCompetitionId + "/new";
        setupPostWithRestResultExpectations(url, OK);
        RestResult<Void> response = service.addNewAssessmentPeriod(newCompetitionId);
        assertNotNull(response);
    }

    @Override
    protected AssessmentPeriodRestServiceImpl registerRestServiceUnderTest() {
        return new AssessmentPeriodRestServiceImpl();
    }
}
